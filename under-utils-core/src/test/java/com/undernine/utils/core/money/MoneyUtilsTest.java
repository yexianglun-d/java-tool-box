package com.undernine.utils.core.money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.*;

/**
 * MoneyUtils 测试类
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
class MoneyUtilsTest {

    // ==================== yuan2Fen() 测试 ====================

    @Test
    void testYuan2Fen() {
        assertThat(MoneyUtils.yuan2Fen(new BigDecimal("10.50"))).isEqualTo(1050L);
        assertThat(MoneyUtils.yuan2Fen(new BigDecimal("0.01"))).isEqualTo(1L);
        assertThat(MoneyUtils.yuan2Fen(new BigDecimal("100"))).isEqualTo(10000L);
        assertThat(MoneyUtils.yuan2Fen(new BigDecimal("0"))).isEqualTo(0L);
    }

    @Test
    void testYuan2Fen_rounding() {
        // 四舍五入
        assertThat(MoneyUtils.yuan2Fen(new BigDecimal("10.504"))).isEqualTo(1050L);
        assertThat(MoneyUtils.yuan2Fen(new BigDecimal("10.505"))).isEqualTo(1051L);
    }

    @Test
    void testYuan2Fen_null() {
        assertThat(MoneyUtils.yuan2Fen(null)).isNull();
    }

    // ==================== fen2Yuan() 测试 ====================

    @Test
    void testFen2Yuan() {
        assertThat(MoneyUtils.fen2Yuan(1050L)).isEqualByComparingTo(new BigDecimal("10.50"));
        assertThat(MoneyUtils.fen2Yuan(1L)).isEqualByComparingTo(new BigDecimal("0.01"));
        assertThat(MoneyUtils.fen2Yuan(10000L)).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(MoneyUtils.fen2Yuan(0L)).isEqualByComparingTo(new BigDecimal("0.00"));
    }

    @Test
    void testFen2Yuan_null() {
        assertThat(MoneyUtils.fen2Yuan(null)).isNull();
    }

    // ==================== add() 测试 ====================

    @Test
    void testAdd_twoValues() {
        BigDecimal result = MoneyUtils.add(new BigDecimal("10.50"), new BigDecimal("20.30"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("30.80"));
    }

    @Test
    void testAdd_twoValues_oneNull() {
        assertThat(MoneyUtils.add(new BigDecimal("10.50"), null))
                .isEqualByComparingTo(new BigDecimal("10.50"));
        assertThat(MoneyUtils.add(null, new BigDecimal("20.30")))
                .isEqualByComparingTo(new BigDecimal("20.30"));
    }

    @Test
    void testAdd_twoValues_bothNull() {
        assertThat(MoneyUtils.add(null, null))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testAdd_multipleValues() {
        BigDecimal result = MoneyUtils.add(
                new BigDecimal("10.50"),
                new BigDecimal("20.30"),
                new BigDecimal("5.00")
        );
        assertThat(result).isEqualByComparingTo(new BigDecimal("35.80"));
    }

    @Test
    void testAdd_multipleValues_withNull() {
        BigDecimal result = MoneyUtils.add(
                new BigDecimal("10.50"),
                null,
                new BigDecimal("20.30")
        );
        assertThat(result).isEqualByComparingTo(new BigDecimal("30.80"));
    }

    @Test
    void testAdd_multipleValues_empty() {
        assertThat(MoneyUtils.add()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testAdd_multipleValues_null() {
        assertThat(MoneyUtils.add((BigDecimal[]) null)).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ==================== subtract() 测试 ====================

    @Test
    void testSubtract() {
        BigDecimal result = MoneyUtils.subtract(new BigDecimal("30.50"), new BigDecimal("10.30"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("20.20"));
    }

    @Test
    void testSubtract_negative() {
        BigDecimal result = MoneyUtils.subtract(new BigDecimal("10.00"), new BigDecimal("20.00"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("-10.00"));
    }

    @Test
    void testSubtract_value1Null() {
        BigDecimal result = MoneyUtils.subtract(null, new BigDecimal("10.00"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("-10.00"));
    }

    @Test
    void testSubtract_value2Null() {
        BigDecimal result = MoneyUtils.subtract(new BigDecimal("10.00"), null);
        assertThat(result).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void testSubtract_bothNull() {
        assertThatThrownBy(() -> MoneyUtils.subtract(null, null))
                .isInstanceOf(NullPointerException.class);
    }

    // ==================== multiply() 测试 ====================

    @Test
    void testMultiply() {
        BigDecimal result = MoneyUtils.multiply(new BigDecimal("10.50"), new BigDecimal("2"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("21.00"));
    }

    @Test
    void testMultiply_decimal() {
        BigDecimal result = MoneyUtils.multiply(new BigDecimal("10.00"), new BigDecimal("1.5"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    void testMultiply_null() {
        assertThatThrownBy(() -> MoneyUtils.multiply(null, new BigDecimal("2")))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> MoneyUtils.multiply(new BigDecimal("10"), null))
                .isInstanceOf(NullPointerException.class);
    }

    // ==================== divide() 测试 ====================

    @Test
    void testDivide_default() {
        BigDecimal result = MoneyUtils.divide(new BigDecimal("10.00"), new BigDecimal("3"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("3.33"));
    }

    @Test
    void testDivide_exact() {
        BigDecimal result = MoneyUtils.divide(new BigDecimal("10.00"), new BigDecimal("2"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    void testDivide_customScale() {
        BigDecimal result = MoneyUtils.divide(
                new BigDecimal("10.00"),
                new BigDecimal("3"),
                4,
                RoundingMode.HALF_UP
        );
        assertThat(result).isEqualByComparingTo(new BigDecimal("3.3333"));
    }

    @Test
    void testDivide_null() {
        assertThatThrownBy(() -> MoneyUtils.divide(null, new BigDecimal("2")))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> MoneyUtils.divide(new BigDecimal("10"), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void testDivide_byZero() {
        assertThatThrownBy(() -> MoneyUtils.divide(new BigDecimal("10"), BigDecimal.ZERO))
                .isInstanceOf(ArithmeticException.class);
    }

    // ==================== compare() 测试 ====================

    @Test
    void testCompare() {
        assertThat(MoneyUtils.compare(new BigDecimal("10.50"), new BigDecimal("20.30"))).isEqualTo(-1);
        assertThat(MoneyUtils.compare(new BigDecimal("20.30"), new BigDecimal("10.50"))).isEqualTo(1);
        assertThat(MoneyUtils.compare(new BigDecimal("10.50"), new BigDecimal("10.50"))).isEqualTo(0);
    }

    @Test
    void testCompare_null() {
        assertThatThrownBy(() -> MoneyUtils.compare(null, new BigDecimal("10")))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> MoneyUtils.compare(new BigDecimal("10"), null))
                .isInstanceOf(NullPointerException.class);
    }

    // ==================== equals() 测试 ====================

    @Test
    void testEquals() {
        assertThat(MoneyUtils.equals(new BigDecimal("10.50"), new BigDecimal("10.50"))).isTrue();
        assertThat(MoneyUtils.equals(new BigDecimal("10.50"), new BigDecimal("10.51"))).isFalse();
    }

    @Test
    void testEquals_null() {
        assertThat(MoneyUtils.equals(null, null)).isTrue();
        assertThat(MoneyUtils.equals(null, new BigDecimal("10"))).isFalse();
        assertThat(MoneyUtils.equals(new BigDecimal("10"), null)).isFalse();
    }

    // ==================== greaterThan() 测试 ====================

    @Test
    void testGreaterThan() {
        assertThat(MoneyUtils.greaterThan(new BigDecimal("20.30"), new BigDecimal("10.50"))).isTrue();
        assertThat(MoneyUtils.greaterThan(new BigDecimal("10.50"), new BigDecimal("20.30"))).isFalse();
        assertThat(MoneyUtils.greaterThan(new BigDecimal("10.50"), new BigDecimal("10.50"))).isFalse();
    }

    // ==================== greaterThanOrEqual() 测试 ====================

    @Test
    void testGreaterThanOrEqual() {
        assertThat(MoneyUtils.greaterThanOrEqual(new BigDecimal("20.30"), new BigDecimal("10.50"))).isTrue();
        assertThat(MoneyUtils.greaterThanOrEqual(new BigDecimal("10.50"), new BigDecimal("10.50"))).isTrue();
        assertThat(MoneyUtils.greaterThanOrEqual(new BigDecimal("10.50"), new BigDecimal("20.30"))).isFalse();
    }

    // ==================== lessThan() 测试 ====================

    @Test
    void testLessThan() {
        assertThat(MoneyUtils.lessThan(new BigDecimal("10.50"), new BigDecimal("20.30"))).isTrue();
        assertThat(MoneyUtils.lessThan(new BigDecimal("20.30"), new BigDecimal("10.50"))).isFalse();
        assertThat(MoneyUtils.lessThan(new BigDecimal("10.50"), new BigDecimal("10.50"))).isFalse();
    }

    // ==================== lessThanOrEqual() 测试 ====================

    @Test
    void testLessThanOrEqual() {
        assertThat(MoneyUtils.lessThanOrEqual(new BigDecimal("10.50"), new BigDecimal("20.30"))).isTrue();
        assertThat(MoneyUtils.lessThanOrEqual(new BigDecimal("10.50"), new BigDecimal("10.50"))).isTrue();
        assertThat(MoneyUtils.lessThanOrEqual(new BigDecimal("20.30"), new BigDecimal("10.50"))).isFalse();
    }

    // ==================== isZero() 测试 ====================

    @Test
    void testIsZero() {
        assertThat(MoneyUtils.isZero(BigDecimal.ZERO)).isTrue();
        assertThat(MoneyUtils.isZero(new BigDecimal("0.00"))).isTrue();
        assertThat(MoneyUtils.isZero(new BigDecimal("0.01"))).isFalse();
        assertThat(MoneyUtils.isZero(null)).isFalse();
    }

    // ==================== isPositive() 测试 ====================

    @Test
    void testIsPositive() {
        assertThat(MoneyUtils.isPositive(new BigDecimal("10.50"))).isTrue();
        assertThat(MoneyUtils.isPositive(new BigDecimal("0.01"))).isTrue();
        assertThat(MoneyUtils.isPositive(BigDecimal.ZERO)).isFalse();
        assertThat(MoneyUtils.isPositive(new BigDecimal("-10.50"))).isFalse();
        assertThat(MoneyUtils.isPositive(null)).isFalse();
    }

    // ==================== isNegative() 测试 ====================

    @Test
    void testIsNegative() {
        assertThat(MoneyUtils.isNegative(new BigDecimal("-10.50"))).isTrue();
        assertThat(MoneyUtils.isNegative(new BigDecimal("-0.01"))).isTrue();
        assertThat(MoneyUtils.isNegative(BigDecimal.ZERO)).isFalse();
        assertThat(MoneyUtils.isNegative(new BigDecimal("10.50"))).isFalse();
        assertThat(MoneyUtils.isNegative(null)).isFalse();
    }

    // ==================== format() 测试 ====================

    @Test
    void testFormat() {
        assertThat(MoneyUtils.format(new BigDecimal("12345.67"))).isEqualTo("12,345.67");
        assertThat(MoneyUtils.format(new BigDecimal("1234567.89"))).isEqualTo("1,234,567.89");
        assertThat(MoneyUtils.format(new BigDecimal("0.50"))).isEqualTo("0.50");
        assertThat(MoneyUtils.format(new BigDecimal("100"))).isEqualTo("100.00");
    }

    @Test
    void testFormat_null() {
        assertThat(MoneyUtils.format(null)).isEqualTo("0.00");
    }

    @Test
    void testFormat_customPattern() {
        String result = MoneyUtils.format(new BigDecimal("12345.67"), "#,##0.00元");
        assertThat(result).isEqualTo("12,345.67元");
    }

    @Test
    void testFormat_customPattern_null() {
        assertThatThrownBy(() -> MoneyUtils.format(new BigDecimal("100"), null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> MoneyUtils.format(new BigDecimal("100"), ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testFormatWithSymbol() {
        assertThat(MoneyUtils.formatWithSymbol(new BigDecimal("12345.67")))
                .isEqualTo("¥12,345.67");
    }

    @Test
    void testFormatWithSymbol_customSymbol() {
        assertThat(MoneyUtils.formatWithSymbol(new BigDecimal("12345.67"), "$"))
                .isEqualTo("$12,345.67");
        assertThat(MoneyUtils.formatWithSymbol(new BigDecimal("12345.67"), null))
                .isEqualTo("¥12,345.67");
    }

    // ==================== max() 测试 ====================

    @Test
    void testMax() {
        assertThat(MoneyUtils.max(new BigDecimal("10.50"), new BigDecimal("20.30")))
                .isEqualByComparingTo(new BigDecimal("20.30"));
        assertThat(MoneyUtils.max(new BigDecimal("20.30"), new BigDecimal("10.50")))
                .isEqualByComparingTo(new BigDecimal("20.30"));
        assertThat(MoneyUtils.max(new BigDecimal("10.50"), new BigDecimal("10.50")))
                .isEqualByComparingTo(new BigDecimal("10.50"));
    }

    @Test
    void testMax_null() {
        assertThatThrownBy(() -> MoneyUtils.max(null, new BigDecimal("10")))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> MoneyUtils.max(new BigDecimal("10"), null))
                .isInstanceOf(NullPointerException.class);
    }

    // ==================== min() 测试 ====================

    @Test
    void testMin() {
        assertThat(MoneyUtils.min(new BigDecimal("10.50"), new BigDecimal("20.30")))
                .isEqualByComparingTo(new BigDecimal("10.50"));
        assertThat(MoneyUtils.min(new BigDecimal("20.30"), new BigDecimal("10.50")))
                .isEqualByComparingTo(new BigDecimal("10.50"));
        assertThat(MoneyUtils.min(new BigDecimal("10.50"), new BigDecimal("10.50")))
                .isEqualByComparingTo(new BigDecimal("10.50"));
    }

    @Test
    void testMin_null() {
        assertThatThrownBy(() -> MoneyUtils.min(null, new BigDecimal("10")))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> MoneyUtils.min(new BigDecimal("10"), null))
                .isInstanceOf(NullPointerException.class);
    }

    // ==================== abs() 测试 ====================

    @Test
    void testAbs() {
        assertThat(MoneyUtils.abs(new BigDecimal("-10.50")))
                .isEqualByComparingTo(new BigDecimal("10.50"));
        assertThat(MoneyUtils.abs(new BigDecimal("10.50")))
                .isEqualByComparingTo(new BigDecimal("10.50"));
        assertThat(MoneyUtils.abs(BigDecimal.ZERO))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testAbs_null() {
        assertThatThrownBy(() -> MoneyUtils.abs(null))
                .isInstanceOf(NullPointerException.class);
    }

    // ==================== negate() 测试 ====================

    @Test
    void testNegate() {
        assertThat(MoneyUtils.negate(new BigDecimal("10.50")))
                .isEqualByComparingTo(new BigDecimal("-10.50"));
        assertThat(MoneyUtils.negate(new BigDecimal("-10.50")))
                .isEqualByComparingTo(new BigDecimal("10.50"));
        assertThat(MoneyUtils.negate(BigDecimal.ZERO))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testNegate_null() {
        assertThatThrownBy(() -> MoneyUtils.negate(null))
                .isInstanceOf(NullPointerException.class);
    }

    // ==================== setScale() 测试 ====================

    @Test
    void testSetScale() {
        assertThat(MoneyUtils.setScale(new BigDecimal("10.5"), 2))
                .isEqualByComparingTo(new BigDecimal("10.50"));
        assertThat(MoneyUtils.setScale(new BigDecimal("10.555"), 2))
                .isEqualByComparingTo(new BigDecimal("10.56"));
        assertThat(MoneyUtils.setScale(new BigDecimal("10.554"), 2))
                .isEqualByComparingTo(new BigDecimal("10.55"));
    }

    @Test
    void testSetScale_null() {
        assertThatThrownBy(() -> MoneyUtils.setScale(null, 2))
                .isInstanceOf(NullPointerException.class);
    }

    // ==================== 综合测试 ====================

    @Test
    void testRoundTrip() {
        // 元 -> 分 -> 元
        BigDecimal yuan = new BigDecimal("123.45");
        Long fen = MoneyUtils.yuan2Fen(yuan);
        BigDecimal yuanAgain = MoneyUtils.fen2Yuan(fen);
        assertThat(yuanAgain).isEqualByComparingTo(yuan);
    }

    @Test
    void testComplexCalculation() {
        // (10.50 + 20.30) * 2 / 3 - 5.00
        BigDecimal step1 = MoneyUtils.add(new BigDecimal("10.50"), new BigDecimal("20.30"));
        BigDecimal step2 = MoneyUtils.multiply(step1, new BigDecimal("2"));
        BigDecimal step3 = MoneyUtils.divide(step2, new BigDecimal("3"));
        BigDecimal result = MoneyUtils.subtract(step3, new BigDecimal("5.00"));

        assertThat(result).isEqualByComparingTo(new BigDecimal("15.53"));
    }
}
