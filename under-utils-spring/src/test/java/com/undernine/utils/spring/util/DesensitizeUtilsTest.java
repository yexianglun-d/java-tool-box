package com.undernine.utils.spring.util;

import com.undernine.utils.spring.enums.SensitiveType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DesensitizeUtils 测试类
 *
 * @author deng
 */
class DesensitizeUtilsTest {

    @Test
    void testMobilePhone() {
        assertThat(DesensitizeUtils.mobilePhone("13812345678")).isEqualTo("138****5678");
        assertThat(DesensitizeUtils.mobilePhone(null)).isNull();
        assertThat(DesensitizeUtils.mobilePhone("123")).isEqualTo("123");
    }

    @Test
    void testIdCard() {
        assertThat(DesensitizeUtils.idCard("320123199001011234")).isEqualTo("320***********1234");
        assertThat(DesensitizeUtils.idCard("320123199001011")).isEqualTo("320********1011");
        assertThat(DesensitizeUtils.idCard(null)).isNull();
        assertThat(DesensitizeUtils.idCard("123")).isEqualTo("123");
    }

    @Test
    void testBankCard() {
        assertThat(DesensitizeUtils.bankCard("6222021234567891234")).isEqualTo("6222***********1234");
        assertThat(DesensitizeUtils.bankCard(null)).isNull();
        assertThat(DesensitizeUtils.bankCard("123")).isEqualTo("123");
    }

    @Test
    void testEmail() {
        assertThat(DesensitizeUtils.email("abc@example.com")).isEqualTo("a***@example.com");
        assertThat(DesensitizeUtils.email("a@example.com")).isEqualTo("a@example.com");
        assertThat(DesensitizeUtils.email(null)).isNull();
        assertThat(DesensitizeUtils.email("invalid")).isEqualTo("invalid");
    }

    @Test
    void testChineseName() {
        assertThat(DesensitizeUtils.chineseName("张三")).isEqualTo("张*");
        assertThat(DesensitizeUtils.chineseName("欧阳锋")).isEqualTo("欧**");
        assertThat(DesensitizeUtils.chineseName("李")).isEqualTo("李");
        assertThat(DesensitizeUtils.chineseName(null)).isNull();
        assertThat(DesensitizeUtils.chineseName("")).isEmpty();
    }

    @Test
    void testAddress() {
        assertThat(DesensitizeUtils.address("北京市海淀区中关村大街1号")).isEqualTo("北京市海淀区***********");
        assertThat(DesensitizeUtils.address("北京")).isEqualTo("北京");
        assertThat(DesensitizeUtils.address(null)).isNull();
    }

    @Test
    void testPassword() {
        assertThat(DesensitizeUtils.password("123456")).isEqualTo("***");
        assertThat(DesensitizeUtils.password("abc")).isEqualTo("***");
        assertThat(DesensitizeUtils.password(null)).isNull();
    }

    @Test
    void testFixedPhone() {
        assertThat(DesensitizeUtils.fixedPhone("010-12345678")).isEqualTo("010-****5678");
        assertThat(DesensitizeUtils.fixedPhone("12345678")).isEqualTo("123**678");
        assertThat(DesensitizeUtils.fixedPhone(null)).isNull();
    }

    @Test
    void testCarLicense() {
        assertThat(DesensitizeUtils.carLicense("京A12345")).isEqualTo("京A1***5");
        assertThat(DesensitizeUtils.carLicense("粤B88888")).isEqualTo("粤B8***8");
        assertThat(DesensitizeUtils.carLicense(null)).isNull();
        assertThat(DesensitizeUtils.carLicense("123")).isEqualTo("123");
    }

    @Test
    void testDesensitizeByType() {
        assertThat(DesensitizeUtils.desensitize("13812345678", SensitiveType.MOBILE_PHONE))
            .isEqualTo("138****5678");
        assertThat(DesensitizeUtils.desensitize("abc@example.com", SensitiveType.EMAIL))
            .isEqualTo("a***@example.com");
        assertThat(DesensitizeUtils.desensitize("张三", SensitiveType.CHINESE_NAME))
            .isEqualTo("张*");
        assertThat(DesensitizeUtils.desensitize("123456", SensitiveType.PASSWORD))
            .isEqualTo("***");
    }

    @Test
    void testDesensitizeCustom() {
        assertThat(DesensitizeUtils.desensitizeCustom("1234567890", "3,4"))
            .isEqualTo("123***7890");
        assertThat(DesensitizeUtils.desensitizeCustom("abcdefgh", "2,2"))
            .isEqualTo("ab****gh");
        assertThat(DesensitizeUtils.desensitizeCustom("test", null))
            .isEqualTo("test");
        assertThat(DesensitizeUtils.desensitizeCustom(null, "3,4"))
            .isNull();
        assertThat(DesensitizeUtils.desensitizeCustom("test", "invalid"))
            .isEqualTo("test");
    }

    @Test
    void testDesensitizeWithNullOrEmpty() {
        assertThat(DesensitizeUtils.desensitize(null, SensitiveType.MOBILE_PHONE)).isNull();
        assertThat(DesensitizeUtils.desensitize("", SensitiveType.MOBILE_PHONE)).isEmpty();
    }
}
