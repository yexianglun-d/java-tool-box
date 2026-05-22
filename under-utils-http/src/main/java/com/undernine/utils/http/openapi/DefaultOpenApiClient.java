package com.undernine.utils.http.openapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.undernine.utils.core.json.JsonUtils;
import com.undernine.utils.http.config.HttpConfig;
import com.undernine.utils.http.enums.HttpMethod;
import com.undernine.utils.http.exception.HttpNetworkException;
import com.undernine.utils.http.exception.HttpTimeoutException;
import com.undernine.utils.http.request.HttpRequest;
import com.undernine.utils.http.response.HttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认开放平台客户端，基于现有 {@link HttpRequest}/{@link HttpResponse} 执行请求。
 */
@Slf4j
public class DefaultOpenApiClient implements OpenApiClient {

    private final OpenApiClientOptions options;
    private final AccessTokenProvider accessTokenProvider;
    private final RequestSigner requestSigner;
    private final ApiErrorDecoder errorDecoder;

    public DefaultOpenApiClient() {
        this(OpenApiClientOptions.defaultOptions());
    }

    public DefaultOpenApiClient(OpenApiClientOptions options) {
        this(options, AccessTokenProvider.noop(), RequestSigner.noop(), ApiErrorDecoder.httpStatus());
    }

    public DefaultOpenApiClient(OpenApiClientOptions options,
                                AccessTokenProvider accessTokenProvider,
                                RequestSigner requestSigner,
                                ApiErrorDecoder errorDecoder) {
        this.options = options != null ? options : OpenApiClientOptions.defaultOptions();
        this.accessTokenProvider = accessTokenProvider != null ? accessTokenProvider : AccessTokenProvider.noop();
        this.requestSigner = requestSigner != null ? requestSigner : RequestSigner.noop();
        this.errorDecoder = errorDecoder != null ? errorDecoder : ApiErrorDecoder.httpStatus();
    }

    @Override
    public OpenApiResponse<String> execute(OpenApiRequest request) {
        return execute(request, String.class);
    }

    @Override
    public <T> OpenApiResponse<T> execute(OpenApiRequest request, Class<T> responseType) {
        return doExecute(request, rawBody -> parseBody(rawBody, responseType));
    }

    @Override
    public <T> OpenApiResponse<T> execute(OpenApiRequest request, TypeReference<T> typeReference) {
        return doExecute(request, rawBody -> JsonUtils.fromJson(rawBody, typeReference));
    }

    private <T> OpenApiResponse<T> doExecute(OpenApiRequest request, BodyParser<T> bodyParser) {
        OpenApiRequest workingRequest = prepareRequest(request);
        int maxRetries = Math.max(0, options.getMaxRetries());
        RuntimeException lastException = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                HttpResponse httpResponse = buildHttpRequest(workingRequest).execute();
                ApiErrorDecodeResult decodeResult = errorDecoder.decode(workingRequest, httpResponse);
                OpenApiResponse<T> openApiResponse = toOpenApiResponse(httpResponse, decodeResult, bodyParser);
                if (openApiResponse.isSuccess() || !openApiResponse.isRetryable() || attempt == maxRetries) {
                    logResult(workingRequest, openApiResponse, attempt);
                    return openApiResponse;
                }
                sleepBeforeRetry(attempt, maxRetries);
            } catch (HttpTimeoutException | HttpNetworkException e) {
                lastException = e;
                if (attempt == maxRetries) {
                    throw new OpenApiException("OpenAPI request failed after retries: " + operationOf(workingRequest), e);
                }
                sleepBeforeRetry(attempt, maxRetries);
            }
        }

        throw new OpenApiException("OpenAPI request failed: " + operationOf(workingRequest), lastException);
    }

    private OpenApiRequest prepareRequest(OpenApiRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("OpenApiRequest cannot be null");
        }
        if (isBlank(request.getUrl())) {
            throw new IllegalArgumentException("OpenApiRequest url cannot be null or empty");
        }

        OpenApiRequest workingRequest = OpenApiRequest.builder()
                .url(request.getUrl())
                .method(request.getMethod())
                .headers(copyOf(request.getHeaders()))
                .query(copyOf(request.getQuery()))
                .body(request.getBody())
                .idempotencyKey(request.getIdempotencyKey())
                .operationName(request.getOperationName())
                .traceId(request.getTraceId())
                .build();
        if (workingRequest.getMethod() == null) {
            workingRequest.setMethod(HttpMethod.GET);
        }

        String token = accessTokenProvider.getAccessToken(workingRequest);
        if (!isBlank(token)) {
            workingRequest.header("Authorization", "Bearer " + token);
        }
        if (!isBlank(workingRequest.getTraceId()) && !isBlank(options.getTraceHeaderName())) {
            workingRequest.header(options.getTraceHeaderName(), workingRequest.getTraceId());
        }
        if (!isBlank(workingRequest.getIdempotencyKey()) && !isBlank(options.getIdempotencyHeaderName())) {
            workingRequest.header(options.getIdempotencyHeaderName(), workingRequest.getIdempotencyKey());
        }

        requestSigner.sign(workingRequest);
        return workingRequest;
    }

    private HttpRequest buildHttpRequest(OpenApiRequest request) {
        return HttpRequest.builder()
                .url(request.getUrl())
                .method(request.getMethod())
                .headers(request.getHeaders())
                .params(request.getQuery())
                .body(request.getBody())
                .retry(0)
                .config(buildHttpConfig())
                .build();
    }

    private HttpConfig buildHttpConfig() {
        return HttpConfig.builder()
                .connectTimeout(options.getConnectTimeout())
                .readTimeout(options.getReadTimeout())
                .writeTimeout(options.getWriteTimeout())
                .maxRetries(0)
                .retryInterval(options.getRetryInterval())
                .loggingEnabled(options.isLoggingEnabled())
                .build();
    }

    private <T> OpenApiResponse<T> toOpenApiResponse(HttpResponse httpResponse,
                                                     ApiErrorDecodeResult decodeResult,
                                                     BodyParser<T> bodyParser) {
        String rawBody = httpResponse.asString();
        boolean success = decodeResult != null && decodeResult.isSuccess();
        T data = null;
        if (success) {
            data = bodyParser.parse(rawBody);
        }
        return OpenApiResponse.<T>builder()
                .success(success)
                .statusCode(httpResponse.getStatusCode())
                .rawBody(rawBody)
                .data(data)
                .errorCode(decodeResult != null ? decodeResult.getErrorCode() : null)
                .errorMessage(decodeResult != null ? decodeResult.getErrorMessage() : null)
                .retryable(decodeResult != null && decodeResult.isRetryable())
                .build();
    }

    @SuppressWarnings("unchecked")
    private <T> T parseBody(String rawBody, Class<T> responseType) {
        if (responseType == null || responseType == Void.class || responseType == Void.TYPE) {
            return null;
        }
        if (responseType == String.class) {
            return (T) rawBody;
        }
        if (isBlank(rawBody)) {
            return null;
        }
        return JsonUtils.fromJson(rawBody, responseType);
    }

    private void sleepBeforeRetry(int attempt, int maxRetries) {
        if (options.getRetryInterval() <= 0) {
            return;
        }
        if (options.isLoggingEnabled()) {
            log.warn("OpenAPI request retrying... ({}/{})", attempt + 1, maxRetries);
        }
        try {
            Thread.sleep(options.getRetryInterval());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OpenApiException("OpenAPI request interrupted", e);
        }
    }

    private <T> void logResult(OpenApiRequest request, OpenApiResponse<T> response, int attempt) {
        if (!options.isLoggingEnabled()) {
            return;
        }
        log.info("OpenAPI response operation={}, status={}, success={}, attempts={}",
                operationOf(request), response.getStatusCode(), response.isSuccess(), attempt + 1);
    }

    private String operationOf(OpenApiRequest request) {
        return !isBlank(request.getOperationName()) ? request.getOperationName() : request.getMethod() + " " + request.getUrl();
    }

    private Map<String, String> copyOf(Map<String, String> source) {
        return source == null ? new HashMap<>() : new HashMap<>(source);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @FunctionalInterface
    private interface BodyParser<T> {
        T parse(String rawBody);
    }
}
