package com.undernine.utils.http.response;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * HttpResponse 测试类
 *
 * @author deng
 */
class HttpResponseTest {

    @Test
    void testIsSuccess() {
        // 2xx 状态码应该返回 true
        HttpResponse response200 = HttpResponse.builder().statusCode(200).build();
        assertThat(response200.isSuccess()).isTrue();

        HttpResponse response201 = HttpResponse.builder().statusCode(201).build();
        assertThat(response201.isSuccess()).isTrue();

        HttpResponse response299 = HttpResponse.builder().statusCode(299).build();
        assertThat(response299.isSuccess()).isTrue();

        // 非 2xx 状态码应该返回 false
        HttpResponse response404 = HttpResponse.builder().statusCode(404).build();
        assertThat(response404.isSuccess()).isFalse();

        HttpResponse response500 = HttpResponse.builder().statusCode(500).build();
        assertThat(response500.isSuccess()).isFalse();
    }

    @Test
    void testIsFail() {
        HttpResponse response200 = HttpResponse.builder().statusCode(200).build();
        assertThat(response200.isFail()).isFalse();

        HttpResponse response404 = HttpResponse.builder().statusCode(404).build();
        assertThat(response404.isFail()).isTrue();
    }

    @Test
    void testAsString() {
        // 测试从 body 字段获取
        HttpResponse response1 = HttpResponse.builder()
                .body("Hello World")
                .build();
        assertThat(response1.asString()).isEqualTo("Hello World");

        // 测试从 bodyBytes 字段获取
        HttpResponse response2 = HttpResponse.builder()
                .bodyBytes("Hello Bytes".getBytes())
                .build();
        assertThat(response2.asString()).isEqualTo("Hello Bytes");

        // 测试空响应
        HttpResponse response3 = HttpResponse.builder().build();
        assertThat(response3.asString()).isNull();
    }

    @Test
    void testAsObject() {
        String json = "{\"name\":\"John\",\"age\":25}";
        HttpResponse response = HttpResponse.builder()
                .body(json)
                .build();

        TestUser user = response.asObject(TestUser.class);
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("John");
        assertThat(user.getAge()).isEqualTo(25);
    }

    @Test
    void testAsObjectWithTypeReference() {
        String json = "[{\"name\":\"John\",\"age\":25},{\"name\":\"Jane\",\"age\":23}]";
        HttpResponse response = HttpResponse.builder()
                .body(json)
                .build();

        List<TestUser> users = response.asObject(new TypeReference<List<TestUser>>() {});
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getName()).isEqualTo("John");
        assertThat(users.get(1).getName()).isEqualTo("Jane");
    }

    @Test
    void testAsObjectWithEmptyBody() {
        HttpResponse response = HttpResponse.builder().build();
        TestUser user = response.asObject(TestUser.class);
        assertThat(user).isNull();
    }

    @Test
    void testAsBytes() {
        byte[] bytes = "Hello Bytes".getBytes();
        HttpResponse response = HttpResponse.builder()
                .bodyBytes(bytes)
                .build();

        assertThat(response.asBytes()).isEqualTo(bytes);
    }

    @Test
    void testSaveToFile(@TempDir Path tempDir) throws IOException {
        File targetFile = tempDir.resolve("test.txt").toFile();
        String content = "Hello File";

        HttpResponse response = HttpResponse.builder()
                .body(content)
                .build();

        response.saveToFile(targetFile);

        assertThat(targetFile).exists();
        String savedContent = Files.readString(targetFile.toPath());
        assertThat(savedContent).isEqualTo(content);
    }

    @Test
    void testSaveToFileWithEmptyBody(@TempDir Path tempDir) {
        File targetFile = tempDir.resolve("test.txt").toFile();
        HttpResponse response = HttpResponse.builder().build();

        assertThatThrownBy(() -> response.saveToFile(targetFile))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Response body is empty");
    }

    @Test
    void testGetHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer token");

        HttpResponse response = HttpResponse.builder()
                .headers(headers)
                .build();

        assertThat(response.getHeader("Content-Type")).isEqualTo("application/json");
        assertThat(response.getHeader("Authorization")).isEqualTo("Bearer token");
        assertThat(response.getHeader("Non-Existent")).isNull();
    }

    @Test
    void testHasHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        HttpResponse response = HttpResponse.builder()
                .headers(headers)
                .build();

        assertThat(response.hasHeader("Content-Type")).isTrue();
        assertThat(response.hasHeader("Non-Existent")).isFalse();
    }

    @Test
    void testHasHeaderWithNullHeaders() {
        HttpResponse response = HttpResponse.builder().build();
        assertThat(response.hasHeader("Content-Type")).isFalse();
    }

    // 测试用的内部类
    public static class TestUser {
        private String name;
        private int age;

        public TestUser() {
        }

        public TestUser(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
