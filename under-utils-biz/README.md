# Under-Utils Biz

可复用业务流程模板模块。

本模块用于放置跨应用反复出现、但又比 `core` 更接近业务流程形态的能力。它不应该包含订单、支付、会员、营销等产品域专属模型。

## 依赖

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-biz</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 导入任务模板

`ImportTaskTemplate` 会让每一行依次经过三个阶段：

1. `parse`
2. `validate`
3. `process`

模板负责收集行级错误并返回导入统计。

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

`CsvImportRowReader` 将 CSV 输入解析为 `CsvRow`，并在模板执行结束后关闭 reader：

```java
try (Reader reader = Files.newBufferedReader(path)) {
    ImportResult result = ImportTaskTemplate.create()
            .execute(CsvImportRowReader.builder(reader)
                    .hasHeader(true)
                    .build(), handler);
}
```

CSV 解析保持小而可预测。Excel、大文件流式导入等场景，建议业务项目自行解析文件，再把行数据交给 `ImportTaskTemplate`。

## 失败语义

- `RowValidationException` 会转换为行级错误。
- parse、validate、process 阶段的其他异常会按阶段转换为行级错误。
- `ImportTaskException` 视为任务级失败，直接向外传播。
- `failFast` 会在首个失败行后停止。
- `maxErrors` 会在收集错误数达到上限后停止。

## 结果字段

`ImportResult` 包含：

- 总行数
- 成功行数
- 失败行数
- 跳过行数
- 行级错误列表

调用方可以根据结果决定提交、回滚、展示预览，或要求用户修正后重试。
