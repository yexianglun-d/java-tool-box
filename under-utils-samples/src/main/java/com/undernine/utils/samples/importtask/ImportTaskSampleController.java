package com.undernine.utils.samples.importtask;

import com.undernine.utils.biz.importtask.CsvImportRowReader;
import com.undernine.utils.biz.importtask.CsvRow;
import com.undernine.utils.biz.importtask.AsyncImportTaskTemplate;
import com.undernine.utils.biz.importtask.ImportErrorCodes;
import com.undernine.utils.biz.importtask.ImportErrorExporter;
import com.undernine.utils.biz.importtask.ImportProgress;
import com.undernine.utils.biz.importtask.ImportResult;
import com.undernine.utils.biz.importtask.ImportRowContext;
import com.undernine.utils.biz.importtask.ImportRowError;
import com.undernine.utils.biz.importtask.ImportRowHandler;
import com.undernine.utils.biz.importtask.ImportTaskTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/samples/import")
public class ImportTaskSampleController {

    private final AsyncImportTaskTemplate asyncImportTaskTemplate;

    public ImportTaskSampleController(AsyncImportTaskTemplate asyncImportTaskTemplate) {
        this.asyncImportTaskTemplate = asyncImportTaskTemplate;
    }

    @PostMapping(value = "/users", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ImportResult importUsers(@RequestBody String csv) {
        CsvImportRowReader reader = CsvImportRowReader.builder(new StringReader(csv))
                .hasHeader(true)
                .build();
        return ImportTaskTemplate.create().execute(reader, userImportHandler());
    }

    @PostMapping(value = "/users/async", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ImportTaskAcceptedResponse importUsersAsync(@RequestBody String csv) {
        CsvImportRowReader reader = CsvImportRowReader.builder(new StringReader(csv))
                .hasHeader(true)
                .build();
        String taskId = asyncImportTaskTemplate.submit(reader, userImportHandler());
        return new ImportTaskAcceptedResponse(taskId);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<ImportProgress> getImportTask(@PathVariable String taskId) {
        return asyncImportTaskTemplate.findProgress(taskId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/tasks/{taskId}/errors.csv", produces = "text/csv")
    public ResponseEntity<String> exportImportErrors(@PathVariable String taskId) {
        return asyncImportTaskTemplate.findResult(taskId)
                .map(result -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"import-errors.csv\"")
                        .body(ImportErrorExporter.toCsv(result.getRowErrors())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.ACCEPTED)
                        .body(ImportErrorExporter.toCsv(List.of())));
    }

    private ImportRowHandler<CsvRow, UserImportRow> userImportHandler() {
        return new ImportRowHandler<>() {
            @Override
            public boolean isBlankRow(CsvRow rawRow) {
                return rawRow == null || rawRow.isBlank();
            }

            @Override
            public UserImportRow parse(CsvRow rawRow, ImportRowContext context) {
                return new UserImportRow(rawRow.get("username"), rawRow.get("phone"));
            }

            @Override
            public List<ImportRowError> validate(UserImportRow row, ImportRowContext context) {
                List<ImportRowError> errors = new ArrayList<>();
                if (isBlank(row.username())) {
                    errors.add(ImportRowError.of("username", ImportErrorCodes.VALIDATION_ERROR, "username is required"));
                }
                if (isBlank(row.phone())) {
                    errors.add(ImportRowError.of("phone", ImportErrorCodes.VALIDATION_ERROR, "phone is required"));
                }
                return errors;
            }

            @Override
            public void process(UserImportRow row, ImportRowContext context) {
                // 真实项目中通常在这里写数据库，或分发到更细的后台任务。
            }
        };
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record UserImportRow(String username, String phone) {
    }

    public record ImportTaskAcceptedResponse(String taskId) {
    }
}
