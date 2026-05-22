package com.undernine.utils.biz.importtask;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImportTaskTemplateTest {

    @Test
    void execute_successImport() {
        List<Item> processedRows = new ArrayList<>();
        ImportTaskTemplate template = ImportTaskTemplate.create();

        ImportResult result = template.execute(List.of("apple,10", "banana,20"), new CsvItemHandler(processedRows));

        assertThat(result.getTotalCount()).isEqualTo(2);
        assertThat(result.getSuccessCount()).isEqualTo(2);
        assertThat(result.getFailureCount()).isZero();
        assertThat(result.getSkippedCount()).isZero();
        assertThat(result.getRowErrors()).isEmpty();
        assertThat(result.isAllSuccess()).isTrue();
        assertThat(processedRows)
                .extracting(Item::name)
                .containsExactly("apple", "banana");
    }

    @Test
    void execute_validationFailureCollectsMultipleErrors() {
        List<Item> processedRows = new ArrayList<>();
        ImportTaskTemplate template = ImportTaskTemplate.create();

        ImportResult result = template.execute(List.of(",0", "apple,10"), new CsvItemHandler(processedRows));

        assertThat(result.getTotalCount()).isEqualTo(2);
        assertThat(result.getSuccessCount()).isEqualTo(1);
        assertThat(result.getFailureCount()).isEqualTo(1);
        assertThat(result.isAllSuccess()).isFalse();
        assertThat(result.getRowErrors()).hasSize(2);
        assertThat(result.getRowErrors())
                .extracting(ImportRowError::getField)
                .containsExactly("name", "quantity");
        assertThat(result.getRowErrors())
                .extracting(ImportRowError::getRowNumber)
                .containsExactly(1, 1);
        assertThat(result.getRowErrors())
                .extracting(ImportRowError::getRawRowSummary)
                .containsExactly(",0", ",0");
        assertThat(processedRows)
                .extracting(Item::name)
                .containsExactly("apple");
    }

    @Test
    void execute_failFastStopsAfterFirstFailedRow() {
        ImportTaskTemplate template = ImportTaskTemplate.create(ImportOptions.builder()
                .failFast(true)
                .build());
        List<Item> processedRows = new ArrayList<>();

        ImportResult result = template.execute(List.of(",0", "apple,10"), new CsvItemHandler(processedRows));

        assertThat(result.getTotalCount()).isEqualTo(1);
        assertThat(result.getSuccessCount()).isZero();
        assertThat(result.getFailureCount()).isEqualTo(1);
        assertThat(result.getRowErrors()).hasSize(2);
        assertThat(processedRows).isEmpty();
    }

    @Test
    void execute_maxErrorsStopsAfterErrorLimitIsReached() {
        ImportTaskTemplate template = ImportTaskTemplate.create(ImportOptions.builder()
                .maxErrors(2)
                .build());

        ImportResult result = template.execute(List.of(",1", ",2", ",3"), new CsvItemHandler(new ArrayList<>()));

        assertThat(result.getTotalCount()).isEqualTo(2);
        assertThat(result.getSuccessCount()).isZero();
        assertThat(result.getFailureCount()).isEqualTo(2);
        assertThat(result.getRowErrors()).hasSize(2);
        assertThat(result.getRowErrors())
                .extracting(ImportRowError::getRowNumber)
                .containsExactly(1, 2);
    }

    @Test
    void execute_skipBlankRows() {
        List<Item> processedRows = new ArrayList<>();
        ImportTaskTemplate template = ImportTaskTemplate.create();

        ImportResult result = template.execute(List.of("", "apple,10", "   ", "banana,20"),
                new CsvItemHandler(processedRows));

        assertThat(result.getTotalCount()).isEqualTo(4);
        assertThat(result.getSuccessCount()).isEqualTo(2);
        assertThat(result.getSkippedCount()).isEqualTo(2);
        assertThat(result.getFailureCount()).isZero();
        assertThat(result.isAllSuccess()).isTrue();
        assertThat(processedRows)
                .extracting(Item::name)
                .containsExactly("apple", "banana");
    }

    @Test
    void execute_processExceptionConvertedToRowError() {
        List<Item> processedRows = new ArrayList<>();
        ImportTaskTemplate template = ImportTaskTemplate.create();

        ImportResult result = template.execute(List.of("apple,10", "boom,1", "banana,20"),
                new CsvItemHandler(processedRows));

        assertThat(result.getTotalCount()).isEqualTo(3);
        assertThat(result.getSuccessCount()).isEqualTo(2);
        assertThat(result.getFailureCount()).isEqualTo(1);
        assertThat(result.getRowErrors()).singleElement().satisfies(error -> {
            assertThat(error.getRowNumber()).isEqualTo(2);
            assertThat(error.getErrorCode()).isEqualTo(ImportErrorCodes.PROCESS_ERROR);
            assertThat(error.getMessage()).isEqualTo("boom item cannot be processed");
            assertThat(error.getRawRowSummary()).isEqualTo("boom,1");
        });
        assertThat(processedRows)
                .extracting(Item::name)
                .containsExactly("apple", "banana");
    }

    @Test
    void execute_rowValidationExceptionConvertedToRowErrors() {
        ImportTaskTemplate template = ImportTaskTemplate.create();
        ImportRowHandler<String, String> handler = new ImportRowHandler<>() {
            @Override
            public String parse(String rawRow, ImportRowContext context) {
                throw new RowValidationException(ImportRowError.of("name", "NAME_INVALID", "name is invalid"));
            }

            @Override
            public void process(String row, ImportRowContext context) {
            }
        };

        ImportResult result = template.execute(List.of("bad"), handler);

        assertThat(result.getTotalCount()).isEqualTo(1);
        assertThat(result.getFailureCount()).isEqualTo(1);
        assertThat(result.getRowErrors()).singleElement().satisfies(error -> {
            assertThat(error.getRowNumber()).isEqualTo(1);
            assertThat(error.getField()).isEqualTo("name");
            assertThat(error.getErrorCode()).isEqualTo("NAME_INVALID");
            assertThat(error.getRawRowSummary()).isEqualTo("bad");
        });
    }

    @Test
    void execute_importTaskExceptionPropagatesAsTaskFailure() {
        ImportTaskTemplate template = ImportTaskTemplate.create();
        ImportRowHandler<String, String> handler = new ImportRowHandler<>() {
            @Override
            public String parse(String rawRow, ImportRowContext context) {
                throw new ImportTaskException("import configuration missing");
            }

            @Override
            public void process(String row, ImportRowContext context) {
            }
        };

        assertThatThrownBy(() -> template.execute(List.of("row"), handler))
                .isInstanceOf(ImportTaskException.class)
                .hasMessage("import configuration missing");
    }

    private record Item(String name, int quantity) {
    }

    private static final class CsvItemHandler implements ImportRowHandler<String, Item> {

        private final List<Item> processedRows;

        private CsvItemHandler(List<Item> processedRows) {
            this.processedRows = processedRows;
        }

        @Override
        public boolean isBlankRow(String rawRow) {
            return rawRow == null || rawRow.isBlank();
        }

        @Override
        public Item parse(String rawRow, ImportRowContext context) {
            String[] parts = rawRow.split(",", -1);
            return new Item(parts[0].trim(), Integer.parseInt(parts[1].trim()));
        }

        @Override
        public List<ImportRowError> validate(Item row, ImportRowContext context) {
            List<ImportRowError> errors = new ArrayList<>();
            if (row.name().isBlank()) {
                errors.add(ImportRowError.of("name", "NAME_REQUIRED", "name is required"));
            }
            if (row.quantity() <= 0) {
                errors.add(ImportRowError.of("quantity", "QUANTITY_POSITIVE", "quantity must be positive"));
            }
            return errors;
        }

        @Override
        public void process(Item row, ImportRowContext context) {
            if ("boom".equals(row.name())) {
                throw new IllegalStateException("boom item cannot be processed");
            }
            processedRows.add(row);
        }
    }
}
