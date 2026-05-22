package com.undernine.utils.biz.importtask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 导入任务模板。
 * <p>
 * 模板顺序执行每一行的 parse、validate、process 阶段，并统一汇总行级错误和导入统计。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ImportTaskTemplate {

    private final ImportOptions options;

    /**
     * 使用默认选项创建模板。
     */
    public ImportTaskTemplate() {
        this(ImportOptions.defaults());
    }

    /**
     * 使用指定选项创建模板。
     *
     * @param options 导入选项
     */
    public ImportTaskTemplate(ImportOptions options) {
        this.options = options == null ? ImportOptions.defaults() : options;
    }

    /**
     * 使用默认选项创建模板。
     *
     * @return 导入模板
     */
    public static ImportTaskTemplate create() {
        return new ImportTaskTemplate();
    }

    /**
     * 使用指定选项创建模板。
     *
     * @param options 导入选项
     * @return 导入模板
     */
    public static ImportTaskTemplate create(ImportOptions options) {
        return new ImportTaskTemplate(options);
    }

    /**
     * 导入 List 行数据。
     *
     * @param rows    原始行列表
     * @param handler 行处理器
     * @param <R>     原始行类型
     * @param <T>     解析后的业务行类型
     * @return 导入结果
     */
    public <R, T> ImportResult execute(List<R> rows, ImportRowHandler<R, T> handler) {
        return execute((Iterable<R>) rows, handler);
    }

    /**
     * 导入行读取器数据。模板会在执行完成或异常退出时关闭读取器。
     *
     * @param rowReader 导入行读取器
     * @param handler   行处理器
     * @param <R>       原始行类型
     * @param <T>       解析后的业务行类型
     * @return 导入结果
     */
    public <R, T> ImportResult execute(ImportRowReader<R> rowReader, ImportRowHandler<R, T> handler) {
        Objects.requireNonNull(rowReader, "rowReader must not be null");
        try (rowReader) {
            return execute((Iterable<R>) rowReader, handler);
        } catch (ImportTaskException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ImportTaskException("Failed to close import row reader", ex);
        }
    }

    /**
     * 导入 Iterable 行数据。
     *
     * @param rows    原始行集合
     * @param handler 行处理器
     * @param <R>     原始行类型
     * @param <T>     解析后的业务行类型
     * @return 导入结果
     */
    public <R, T> ImportResult execute(Iterable<R> rows, ImportRowHandler<R, T> handler) {
        Objects.requireNonNull(rows, "rows must not be null");
        Objects.requireNonNull(handler, "handler must not be null");

        ImportAccumulator accumulator = new ImportAccumulator();
        Iterator<R> iterator = iterator(rows);

        while (hasNext(iterator)) {
            R rawRow = next(iterator);
            accumulator.totalCount++;
            ImportRowContext context = new ImportRowContext(accumulator.totalCount);

            if (options.isSkipBlankRows() && isBlankRow(handler, rawRow)) {
                accumulator.skippedCount++;
                continue;
            }

            RowOutcome outcome = processRow(rawRow, context, handler);
            if (outcome.isSuccess()) {
                accumulator.successCount++;
                continue;
            }

            accumulator.failureCount++;
            addErrors(accumulator.rowErrors, outcome.getErrors());
            if (shouldStop(accumulator)) {
                break;
            }
        }

        return new ImportResult(
                accumulator.totalCount,
                accumulator.successCount,
                accumulator.failureCount,
                accumulator.skippedCount,
                accumulator.rowErrors
        );
    }

    /**
     * 获取模板选项。
     *
     * @return 导入选项
     */
    public ImportOptions getOptions() {
        return options;
    }

    private <R, T> RowOutcome processRow(R rawRow, ImportRowContext context, ImportRowHandler<R, T> handler) {
        String rawRowSummary = summarizeRawRow(handler, rawRow);
        T parsedRow;
        try {
            parsedRow = handler.parse(rawRow, context);
        } catch (ImportTaskException ex) {
            throw ex;
        } catch (RowValidationException ex) {
            return RowOutcome.failure(toErrors(ex, context, rawRowSummary, ImportErrorCodes.PARSE_ERROR));
        } catch (Exception ex) {
            return RowOutcome.failure(singleError(context, rawRowSummary, ImportErrorCodes.PARSE_ERROR, ex));
        }

        try {
            List<ImportRowError> validationErrors = handler.validate(parsedRow, context);
            if (validationErrors != null && !validationErrors.isEmpty()) {
                List<ImportRowError> normalizedErrors = normalizeErrors(validationErrors, context, rawRowSummary);
                if (!normalizedErrors.isEmpty()) {
                    return RowOutcome.failure(normalizedErrors);
                }
            }
        } catch (ImportTaskException ex) {
            throw ex;
        } catch (RowValidationException ex) {
            return RowOutcome.failure(toErrors(ex, context, rawRowSummary, ImportErrorCodes.VALIDATION_ERROR));
        } catch (Exception ex) {
            return RowOutcome.failure(singleError(context, rawRowSummary, ImportErrorCodes.VALIDATION_ERROR, ex));
        }

        try {
            handler.process(parsedRow, context);
            return RowOutcome.success();
        } catch (ImportTaskException ex) {
            throw ex;
        } catch (RowValidationException ex) {
            return RowOutcome.failure(toErrors(ex, context, rawRowSummary, ImportErrorCodes.PROCESS_ERROR));
        } catch (Exception ex) {
            return RowOutcome.failure(singleError(context, rawRowSummary, ImportErrorCodes.PROCESS_ERROR, ex));
        }
    }

    private <R, T> boolean isBlankRow(ImportRowHandler<R, T> handler, R rawRow) {
        try {
            return handler.isBlankRow(rawRow);
        } catch (ImportTaskException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ImportTaskException("Failed to determine whether import row is blank", ex);
        }
    }

    private <R, T> String summarizeRawRow(ImportRowHandler<R, T> handler, R rawRow) {
        try {
            return handler.summarizeRawRow(rawRow);
        } catch (ImportTaskException ex) {
            throw ex;
        } catch (Exception ex) {
            return null;
        }
    }

    private <R> Iterator<R> iterator(Iterable<R> rows) {
        try {
            return rows.iterator();
        } catch (ImportTaskException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ImportTaskException("Failed to create import row iterator", ex);
        }
    }

    private <R> boolean hasNext(Iterator<R> iterator) {
        try {
            return iterator.hasNext();
        } catch (ImportTaskException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ImportTaskException("Failed to read next import row", ex);
        }
    }

    private <R> R next(Iterator<R> iterator) {
        try {
            return iterator.next();
        } catch (ImportTaskException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ImportTaskException("Failed to read next import row", ex);
        }
    }

    private List<ImportRowError> toErrors(RowValidationException exception, ImportRowContext context,
                                         String rawRowSummary, String fallbackErrorCode) {
        if (!exception.getRowErrors().isEmpty()) {
            List<ImportRowError> normalizedErrors = normalizeErrors(exception.getRowErrors(), context, rawRowSummary);
            if (!normalizedErrors.isEmpty()) {
                return normalizedErrors;
            }
        }
        String message = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
        return Collections.singletonList(new ImportRowError(
                context.getRowNumber(),
                null,
                fallbackErrorCode,
                message,
                rawRowSummary
        ));
    }

    private List<ImportRowError> singleError(ImportRowContext context, String rawRowSummary,
                                             String errorCode, Exception exception) {
        String message = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
        return Collections.singletonList(new ImportRowError(
                context.getRowNumber(),
                null,
                errorCode,
                message,
                rawRowSummary
        ));
    }

    private List<ImportRowError> normalizeErrors(List<ImportRowError> errors, ImportRowContext context,
                                                String rawRowSummary) {
        List<ImportRowError> normalizedErrors = new ArrayList<>(errors.size());
        for (ImportRowError error : errors) {
            if (error == null) {
                continue;
            }
            normalizedErrors.add(error.withRowMetadata(context.getRowNumber(), rawRowSummary));
        }
        return normalizedErrors;
    }

    private void addErrors(List<ImportRowError> collectedErrors, List<ImportRowError> newErrors) {
        for (ImportRowError error : newErrors) {
            if (options.isErrorLimitReached(collectedErrors.size())) {
                return;
            }
            collectedErrors.add(error);
        }
    }

    private boolean shouldStop(ImportAccumulator accumulator) {
        return options.isFailFast() || options.isErrorLimitReached(accumulator.rowErrors.size());
    }

    private static final class ImportAccumulator {

        private int totalCount;
        private int successCount;
        private int failureCount;
        private int skippedCount;
        private final List<ImportRowError> rowErrors = new ArrayList<>();
    }

    private static final class RowOutcome {

        private static final RowOutcome SUCCESS = new RowOutcome(true, Collections.emptyList());

        private final boolean success;
        private final List<ImportRowError> errors;

        private RowOutcome(boolean success, List<ImportRowError> errors) {
            this.success = success;
            this.errors = errors;
        }

        private static RowOutcome success() {
            return SUCCESS;
        }

        private static RowOutcome failure(List<ImportRowError> errors) {
            return new RowOutcome(false, errors == null ? Collections.emptyList() : errors);
        }

        private boolean isSuccess() {
            return success;
        }

        private List<ImportRowError> getErrors() {
            return errors;
        }
    }
}
