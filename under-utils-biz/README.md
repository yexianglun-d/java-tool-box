# Under-Utils Biz

Reusable business-workflow templates.

The module is for patterns that appear across applications but are still too domain-shaped for `core`. It should not contain product-specific models such as orders, payments, membership, or marketing rules.

## Dependency

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-biz</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Import Task Template

`ImportTaskTemplate` runs each row through three phases:

1. `parse`
2. `validate`
3. `process`

It collects row-level errors and returns import statistics.

```java
ImportTaskTemplate template = ImportTaskTemplate.create(
        ImportOptions.builder()
                .skipBlankRows(true)
                .maxErrors(100)
                .build()
);

ImportResult result = template.execute(rows, new ImportRowHandler<String, UserImportRow>() {

    @Override
    public boolean isBlankRow(String rawRow) {
        return rawRow == null || rawRow.isBlank();
    }

    @Override
    public UserImportRow parse(String rawRow, ImportRowContext context) {
        String[] parts = rawRow.split(",", -1);
        return new UserImportRow(parts[0].trim(), parts[1].trim());
    }

    @Override
    public List<ImportRowError> validate(UserImportRow row, ImportRowContext context) {
        List<ImportRowError> errors = new ArrayList<>();
        if (row.username().isBlank()) {
            errors.add(ImportRowError.of("username", "USERNAME_REQUIRED", "username is required"));
        }
        if (row.phone().isBlank()) {
            errors.add(ImportRowError.of("phone", "PHONE_REQUIRED", "phone is required"));
        }
        return errors;
    }

    @Override
    public void process(UserImportRow row, ImportRowContext context) {
        userService.importUser(row);
    }
});
```

## CSV Reader

`CsvImportRowReader` parses CSV input into `CsvRow` and closes the reader after template execution:

```java
try (Reader reader = Files.newBufferedReader(path)) {
    ImportResult result = ImportTaskTemplate.create()
            .execute(CsvImportRowReader.builder(reader)
                    .hasHeader(true)
                    .build(), handler);
}
```

CSV parsing is intentionally small and predictable. For Excel files, streaming uploads, or very large imports, keep file-specific parsing in the application and feed rows into `ImportTaskTemplate`.

## Failure Semantics

- `RowValidationException` is converted to row errors.
- Other parse, validate, or process exceptions are converted to row errors with phase-specific codes.
- `ImportTaskException` is treated as a task-level failure and is propagated.
- `failFast` stops after the first failed row.
- `maxErrors` stops once the collected error count reaches the limit.

## Result Fields

`ImportResult` reports:

- total row count
- success count
- failure count
- skipped count
- collected row errors

Use the returned result to decide whether to commit, roll back, display a preview, or ask the user to fix and retry.
