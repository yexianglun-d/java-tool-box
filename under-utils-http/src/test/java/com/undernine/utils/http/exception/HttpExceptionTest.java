package com.undernine.utils.http.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HTTP 异常测试类
 *
 * @author deng
 */
class HttpExceptionTest {

    @Test
    void testHttpExceptionWithMessage() {
        HttpException exception = new HttpException("Test error");

        assertThat(exception.getMessage()).isEqualTo("Test error");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void testHttpExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        HttpException exception = new HttpException("Test error", cause);

        assertThat(exception.getMessage()).isEqualTo("Test error");
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void testHttpExceptionWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        HttpException exception = new HttpException(cause);

        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void testHttpTimeoutException() {
        HttpTimeoutException exception = new HttpTimeoutException("Timeout error");

        assertThat(exception).isInstanceOf(HttpException.class);
        assertThat(exception.getMessage()).isEqualTo("Timeout error");
    }

    @Test
    void testHttpTimeoutExceptionWithCause() {
        Throwable cause = new java.net.SocketTimeoutException("Socket timeout");
        HttpTimeoutException exception = new HttpTimeoutException("Timeout error", cause);

        assertThat(exception).isInstanceOf(HttpException.class);
        assertThat(exception.getMessage()).isEqualTo("Timeout error");
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void testHttpNetworkException() {
        HttpNetworkException exception = new HttpNetworkException("Network error");

        assertThat(exception).isInstanceOf(HttpException.class);
        assertThat(exception.getMessage()).isEqualTo("Network error");
    }

    @Test
    void testHttpNetworkExceptionWithCause() {
        Throwable cause = new java.io.IOException("Connection refused");
        HttpNetworkException exception = new HttpNetworkException("Network error", cause);

        assertThat(exception).isInstanceOf(HttpException.class);
        assertThat(exception.getMessage()).isEqualTo("Network error");
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
