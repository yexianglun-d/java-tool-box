package com.undernine.utils.samples.importtask;

import com.undernine.utils.biz.importtask.CsvImportRowReader;
import com.undernine.utils.biz.importtask.CsvRow;
import com.undernine.utils.biz.importtask.ImportErrorCodes;
import com.undernine.utils.biz.importtask.ImportResult;
import com.undernine.utils.biz.importtask.ImportRowContext;
import com.undernine.utils.biz.importtask.ImportRowError;
import com.undernine.utils.biz.importtask.ImportRowHandler;
import com.undernine.utils.biz.importtask.ImportTaskTemplate;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/users", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ImportResult importUsers(@RequestBody String csv) {
        CsvImportRowReader reader = CsvImportRowReader.builder(new StringReader(csv))
                .hasHeader(true)
                .build();
        return ImportTaskTemplate.create().execute(reader, new ImportRowHandler<CsvRow, UserImportRow>() {
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
                // Replace this with database write or background task dispatch in a real application.
            }
        });
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record UserImportRow(String username, String phone) {
    }
}
