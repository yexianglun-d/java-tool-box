package com.undernine.utils.biz.importtask;

import java.io.IOException;
import java.util.List;

/**
 * 导入行级错误导出工具。
 * <p>
 * 目前提供无外部依赖的 CSV 导出，方便 Web 层直接返回错误文件或写入对象存储。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.1
 * @since 1.0.1
 */
public final class ImportErrorExporter {

    private static final String[] HEADERS = {"rowNumber", "field", "errorCode", "message", "rawRowSummary"};

    private ImportErrorExporter() {
    }

    /**
     * 将行级错误导出为 CSV 字符串。
     *
     * @param errors 行级错误列表
     * @return CSV 内容
     */
    public static String toCsv(List<ImportRowError> errors) {
        StringBuilder builder = new StringBuilder();
        try {
            writeCsv(errors, builder);
        } catch (IOException e) {
            throw new IllegalStateException("StringBuilder should not throw IOException", e);
        }
        return builder.toString();
    }

    /**
     * 将行级错误写入 CSV。
     *
     * @param errors 行级错误列表
     * @param out    输出目标
     * @throws IOException 写入失败时抛出
     */
    public static void writeCsv(List<ImportRowError> errors, Appendable out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("out must not be null");
        }
        writeLine(out, HEADERS);
        if (errors == null || errors.isEmpty()) {
            return;
        }
        for (ImportRowError error : errors) {
            if (error == null) {
                continue;
            }
            writeLine(out,
                    String.valueOf(error.getRowNumber()),
                    error.getField(),
                    error.getErrorCode(),
                    error.getMessage(),
                    error.getRawRowSummary()
            );
        }
    }

    private static void writeLine(Appendable out, String... values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                out.append(',');
            }
            out.append(escape(values[i]));
        }
        out.append('\n');
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        boolean shouldQuote = value.indexOf(',') >= 0 || value.indexOf('"') >= 0
                || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0;
        if (!shouldQuote) {
            return value;
        }
        return '"' + value.replace("\"", "\"\"") + '"';
    }
}
