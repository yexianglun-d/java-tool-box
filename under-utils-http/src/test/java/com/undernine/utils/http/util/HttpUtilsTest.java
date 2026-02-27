package com.undernine.utils.http.util;

import com.undernine.utils.http.config.HttpConfig;
import com.undernine.utils.http.response.HttpResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HttpUtils 测试类
 *
 * @author deng
 */
class HttpUtilsTest {

    private MockWebServer mockWebServer;
    private String baseUrl;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        baseUrl = mockWebServer.url("/").toString();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testGet() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("Hello World")
                .setResponseCode(200));

        String result = HttpUtils.get(baseUrl + "test");

        assertThat(result).isEqualTo("Hello World");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/test");
    }

    @Test
    void testGetWithParams() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("Success")
                .setResponseCode(200));

        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("size", "10");

        String result = HttpUtils.get(baseUrl + "users", params);

        assertThat(result).isEqualTo("Success");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).contains("page=1");
        assertThat(request.getPath()).contains("size=10");
    }

    @Test
    void testGetWithHeaders() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("Success")
                .setResponseCode(200));

        Map<String, String> params = new HashMap<>();
        params.put("page", "1");

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token");

        String result = HttpUtils.get(baseUrl + "users", params, headers);

        assertThat(result).isEqualTo("Success");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getHeader("Authorization")).isEqualTo("Bearer token");
    }

    @Test
    void testGetResponse() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("Hello World")
                .setResponseCode(200)
                .addHeader("Content-Type", "text/plain"));

        HttpResponse response = HttpUtils.getResponse(baseUrl + "test");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.asString()).isEqualTo("Hello World");
        assertThat(response.getHeader("Content-Type")).isEqualTo("text/plain");
    }

    @Test
    void testPostJson() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":1,\"name\":\"John\"}")
                .setResponseCode(201));

        TestUser user = new TestUser("John", 25);
        String result = HttpUtils.postJson(baseUrl + "users", user);

        assertThat(result).contains("\"id\":1");
        assertThat(result).contains("\"name\":\"John\"");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getHeader("Content-Type")).contains("application/json");
        assertThat(request.getBody().readUtf8()).contains("\"name\":\"John\"");
    }

    @Test
    void testPostForm() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("Login Success")
                .setResponseCode(200));

        Map<String, String> formParams = new HashMap<>();
        formParams.put("username", "john");
        formParams.put("password", "123456");

        String result = HttpUtils.postForm(baseUrl + "login", formParams);

        assertThat(result).isEqualTo("Login Success");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        String body = request.getBody().readUtf8();
        assertThat(body).contains("username=john");
        assertThat(body).contains("password=123456");
    }

    @Test
    void testPostResponse() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":1}")
                .setResponseCode(201));

        TestUser user = new TestUser("John", 25);
        HttpResponse response = HttpUtils.postResponse(baseUrl + "users", user);

        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.asString()).contains("\"id\":1");
    }

    @Test
    void testPutJson() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":1,\"name\":\"John Updated\"}")
                .setResponseCode(200));

        TestUser user = new TestUser("John Updated", 26);
        String result = HttpUtils.putJson(baseUrl + "users/1", user);

        assertThat(result).contains("John Updated");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("PUT");
        assertThat(request.getHeader("Content-Type")).contains("application/json");
    }

    @Test
    void testPutResponse() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":1}")
                .setResponseCode(200));

        TestUser user = new TestUser("John", 25);
        HttpResponse response = HttpUtils.putResponse(baseUrl + "users/1", user);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Test
    void testDelete() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(204));  // 204 No Content 不应该有 body

        String result = HttpUtils.delete(baseUrl + "users/1");

        // 204 响应可能返回空字符串
        assertThat(result).isNotNull();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("DELETE");
    }

    @Test
    void testDeleteResponse() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(204));

        HttpResponse response = HttpUtils.deleteResponse(baseUrl + "users/1");

        assertThat(response.getStatusCode()).isEqualTo(204);
    }

    @Test
    void testUpload(@TempDir Path tempDir) throws IOException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"fileId\":\"123\"}")
                .setResponseCode(200));

        // 创建临时文件
        File file = tempDir.resolve("test.txt").toFile();
        Files.writeString(file.toPath(), "Test file content");

        String result = HttpUtils.upload(baseUrl + "upload", "file", file);

        assertThat(result).contains("fileId");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getHeader("Content-Type")).contains("multipart/form-data");
    }

    @Test
    void testUploadWithParams(@TempDir Path tempDir) throws IOException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"fileId\":\"123\"}")
                .setResponseCode(200));

        File file = tempDir.resolve("test.txt").toFile();
        Files.writeString(file.toPath(), "Test file content");

        Map<String, String> params = new HashMap<>();
        params.put("description", "My file");

        String result = HttpUtils.upload(baseUrl + "upload", "file", file, params);

        assertThat(result).contains("fileId");

        RecordedRequest request = mockWebServer.takeRequest();
        String body = request.getBody().readUtf8();
        assertThat(body).contains("description");
        assertThat(body).contains("My file");
    }

    @Test
    void testDownload(@TempDir Path tempDir) throws IOException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("File content")
                .setResponseCode(200));

        File targetFile = tempDir.resolve("downloaded.txt").toFile();
        HttpUtils.download(baseUrl + "files/123", targetFile);

        assertThat(targetFile).exists();
        String content = Files.readString(targetFile.toPath());
        assertThat(content).isEqualTo("File content");
    }

    @Test
    void testUrlEncode() {
        String encoded = HttpUtils.urlEncode("Hello World");
        assertThat(encoded).isEqualTo("Hello+World");

        String encoded2 = HttpUtils.urlEncode("测试中文");
        assertThat(encoded2).isNotEqualTo("测试中文");
    }

    @Test
    void testUrlDecode() {
        String decoded = HttpUtils.urlDecode("Hello+World");
        assertThat(decoded).isEqualTo("Hello World");
    }

    @Test
    void testSetDefaultConfig() {
        HttpConfig config = HttpConfig.builder()
                .connectTimeout(3000)
                .readTimeout(8000)
                .build();

        HttpUtils.setDefaultConfig(config);

        HttpConfig retrievedConfig = HttpUtils.getDefaultConfig();
        assertThat(retrievedConfig).isEqualTo(config);
        assertThat(retrievedConfig.getConnectTimeout()).isEqualTo(3000);
        assertThat(retrievedConfig.getReadTimeout()).isEqualTo(8000);

        // 恢复默认配置
        HttpUtils.setDefaultConfig(HttpConfig.defaultConfig());
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
