package com.undernine.utils.spring.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undernine.utils.spring.annotation.Sensitive;
import com.undernine.utils.spring.enums.SensitiveType;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SensitiveJsonSerializer 测试类
 *
 * @author deng
 */
class SensitiveJsonSerializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testMobilePhoneSerialization() throws JsonProcessingException {
        TestUser user = new TestUser();
        user.setPhone("13812345678");

        String json = objectMapper.writeValueAsString(user);

        assertThat(json).contains("138****5678");
    }

    @Test
    void testIdCardSerialization() throws JsonProcessingException {
        TestUser user = new TestUser();
        user.setIdCard("320123199001011234");

        String json = objectMapper.writeValueAsString(user);

        assertThat(json).contains("320***********1234");
    }

    @Test
    void testEmailSerialization() throws JsonProcessingException {
        TestUser user = new TestUser();
        user.setEmail("abc@example.com");

        String json = objectMapper.writeValueAsString(user);

        assertThat(json).contains("a***@example.com");
    }

    @Test
    void testNullValue() throws JsonProcessingException {
        TestUser user = new TestUser();
        user.setPhone(null);

        String json = objectMapper.writeValueAsString(user);

        assertThat(json).doesNotContain("phone");
    }

    @Data
    static class TestUser {
        @Sensitive(type = SensitiveType.MOBILE_PHONE)
        private String phone;

        @Sensitive(type = SensitiveType.ID_CARD)
        private String idCard;

        @Sensitive(type = SensitiveType.EMAIL)
        private String email;
    }
}
