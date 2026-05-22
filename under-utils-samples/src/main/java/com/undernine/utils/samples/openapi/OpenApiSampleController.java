package com.undernine.utils.samples.openapi;

import com.undernine.utils.http.enums.HttpMethod;
import com.undernine.utils.http.openapi.ApiErrorDecoder;
import com.undernine.utils.http.openapi.DefaultOpenApiClient;
import com.undernine.utils.http.openapi.OpenApiClient;
import com.undernine.utils.http.openapi.OpenApiClientOptions;
import com.undernine.utils.http.openapi.OpenApiRequest;
import com.undernine.utils.http.openapi.OpenApiResponse;
import com.undernine.utils.http.openapi.RequestSigner;
import com.undernine.utils.spring.context.OperationContext;
import com.undernine.utils.spring.context.OperationContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/samples/openapi")
public class OpenApiSampleController {

    @PostMapping("/orders")
    public OpenApiResponse<GatewayOrderResponse> createOrder(@RequestBody GatewayOrderCommand command) {
        String gatewayUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/samples/openapi/mock-gateway/orders")
                .toUriString();
        OperationContext context = OperationContextHolder.getContext();

        RequestSigner signer = request -> request.header("X-Signature", "sample-signature");
        OpenApiClient client = new DefaultOpenApiClient(
                OpenApiClientOptions.builder()
                        .connectTimeout(2000)
                        .readTimeout(2000)
                        .maxRetries(1)
                        .loggingEnabled(true)
                        .build(),
                request -> "sample-token",
                signer,
                ApiErrorDecoder.httpStatus()
        );

        OpenApiRequest request = OpenApiRequest.builder()
                .url(gatewayUrl)
                .method(HttpMethod.POST)
                .body(command)
                .traceId(context == null ? null : context.getTraceId())
                .idempotencyKey(command.requestNo())
                .operationName("sampleCreateOrder")
                .build();
        return client.execute(request, GatewayOrderResponse.class);
    }

    @PostMapping("/mock-gateway/orders")
    public GatewayOrderResponse mockGateway(@RequestBody GatewayOrderCommand command,
                                            @RequestHeader Map<String, String> headers) {
        return new GatewayOrderResponse(
                "remote-" + command.requestNo(),
                header(headers, "Authorization"),
                header(headers, "X-Signature"),
                header(headers, "X-Trace-Id"),
                header(headers, "Idempotency-Key")
        );
    }

    private static String header(Map<String, String> headers, String name) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return "";
    }

    public record GatewayOrderCommand(String requestNo, String skuId, int quantity) {
    }

    public record GatewayOrderResponse(String remoteOrderId,
                                       String authorization,
                                       String signature,
                                       String traceId,
                                       String idempotencyKey) {
    }
}
