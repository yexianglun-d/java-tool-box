package com.undernine.utils.samples.context;

import com.undernine.utils.spring.context.OperationContext;
import com.undernine.utils.spring.context.OperationContextExecutors;
import com.undernine.utils.spring.context.OperationContextHolder;
import com.undernine.utils.spring.context.OperationContextSnapshot;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/samples/context")
public class ContextSampleController {

    private final ExecutorService executorService;

    public ContextSampleController(ExecutorService executorService) {
        this.executorService = OperationContextExecutors.wrap(executorService);
    }

    @GetMapping("/current")
    public Map<String, Object> current() {
        return describe(OperationContextHolder.getContext());
    }

    @GetMapping("/async")
    public Map<String, Object> async() throws Exception {
        OperationContextSnapshot snapshot = OperationContextSnapshot.capture();
        Map<String, Object> snapshotResult = snapshot.wrapSupplier(() -> describe(OperationContextHolder.getContext())).get();
        Map<String, Object> executorResult = executorService.submit(() -> describe(OperationContextHolder.getContext())).get();
        return Map.of(
                "current", describe(OperationContextHolder.getContext()),
                "snapshot", snapshotResult,
                "executor", executorResult
        );
    }

    private static Map<String, Object> describe(OperationContext context) {
        if (context == null) {
            return Map.of("present", false);
        }
        return Map.of(
                "present", true,
                "traceId", nullToEmpty(context.getTraceId()),
                "tenantId", nullToEmpty(context.getTenantId()),
                "userId", nullToEmpty(context.getUserId()),
                "requestMethod", nullToEmpty(context.getRequestMethod()),
                "requestUri", nullToEmpty(context.getRequestUri()),
                "clientIp", nullToEmpty(context.getClientIp()),
                "operationName", nullToEmpty(context.getOperationName()),
                "attributes", context.getAttributes()
        );
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
