package com.undernine.utils.http.request;

import com.undernine.utils.http.config.HttpConfig;
import com.undernine.utils.http.enums.HttpMethod;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * HttpRequest 测试类
 *
 * @author deng
 */
class HttpRequestTest {

    @Test
    void testBuilderWithUrl() {
        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/users")
                .build();

        assertThat(request.getUrl()).isEqualTo("https://api.example.com/users");
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET); // 默认 GET
    }

    @Test
    void testBuilderWithMethod() {
        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/users")
                .method(HttpMethod.POST)
                .build();

        assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
    }

    @Test
    void testBuilderWithHeaders() {
        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/users")
                .header("Authorization", "Bearer token")
                .header("Content-Type", "application/json")
                .build();

        assertThat(request.getHeaders()).containsEntry("Authorization", "Bearer token");
        assertThat(request.getHeaders()).containsEntry("Content-Type", "application/json");
    }

    @Test
    void testBuilderWithHeadersMap() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token");
        headers.put("Content-Type", "application/json");

        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/users")
                .headers(headers)
                .build();

        assertThat(request.getHeaders()).containsAllEntriesOf(headers);
    }

    @Test
    void testBuilderWithParams() {
        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/users")
                .param("page", "1")
                .param("size", "10")
                .build();

        assertThat(request.getParams()).containsEntry("page", "1");
        assertThat(request.getParams()).containsEntry("size", "10");
    }

    @Test
    void testBuilderWithParamsMap() {
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("size", "10");

        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/users")
                .params(params)
                .build();

        assertThat(request.getParams()).containsAllEntriesOf(params);
    }

    @Test
    void testBuilderWithBody() {
        TestUser user = new TestUser("John", 25);

        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/users")
                .method(HttpMethod.POST)
                .body(user)
                .build();

        assertThat(request.getBody()).isEqualTo(user);
    }

    @Test
    void testBuilderWithFile() {
        File file = new File("test.txt");

        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/upload")
                .method(HttpMethod.POST)
                .file("file", file)
                .build();

        assertThat(request.getFiles()).containsEntry("file", file);
    }

    @Test
    void testBuilderWithFormParams() {
        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/login")
                .method(HttpMethod.POST)
                .formParam("username", "john")
                .formParam("password", "123456")
                .build();

        assertThat(request.getFormParams()).containsEntry("username", "john");
        assertThat(request.getFormParams()).containsEntry("password", "123456");
    }

    @Test
    void testBuilderWithFormParamsMap() {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("username", "john");
        formParams.put("password", "123456");

        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/login")
                .method(HttpMethod.POST)
                .formParams(formParams)
                .build();

        assertThat(request.getFormParams()).containsAllEntriesOf(formParams);
    }

    @Test
    void testBuilderWithTimeout() {
        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/users")
                .timeout(5000)
                .build();

        assertThat(request.getTimeout()).isEqualTo(5000);
    }

    @Test
    void testBuilderWithRetry() {
        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/users")
                .retry(3)
                .build();

        assertThat(request.getMaxRetries()).isEqualTo(3);
    }

    @Test
    void testBuilderWithConfig() {
        HttpConfig config = HttpConfig.builder()
                .connectTimeout(5000)
                .readTimeout(10000)
                .build();

        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/users")
                .config(config)
                .build();

        assertThat(request.getConfig()).isEqualTo(config);
    }

    @Test
    void testBuilderWithoutUrl() {
        assertThatThrownBy(() -> HttpRequest.builder().build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("URL cannot be null or empty");
    }

    @Test
    void testBuilderWithEmptyUrl() {
        assertThatThrownBy(() -> HttpRequest.builder().url("").build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("URL cannot be null or empty");
    }

    @Test
    void testBuilderWithNullHeadersMap() {
        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/users")
                .headers(null)
                .build();

        assertThat(request.getHeaders()).isEmpty();
    }

    @Test
    void testBuilderWithNullParamsMap() {
        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/users")
                .params(null)
                .build();

        assertThat(request.getParams()).isEmpty();
    }

    @Test
    void testBuilderWithNullFormParamsMap() {
        HttpRequest request = HttpRequest.builder()
                .url("https://api.example.com/users")
                .formParams(null)
                .build();

        assertThat(request.getFormParams()).isEmpty();
    }

    // 测试用的内部类
    public static class TestUser {
        private String name;
        private int age;

        public TestUser(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
}
