package com.undernine.utils.test.examples;

import com.undernine.utils.core.money.MoneyUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * MoneyUtils 使用示例
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class MoneyUtilsExample {

    public static void main(String[] args) {
        System.out.println("========== MoneyUtils 使用示例 ==========\n");

        // 1. 元分转换
        unitConversion();

        // 2. 基础运算
        basicOperations();

        // 3. 金额比较
        comparison();

        // 4. 格式化显示
        formatting();

        // 5. 其他工具方法
        otherMethods();

        // 6. 实际应用场景
        practicalUseCases();
    }

    /**
     * 1. 元分转换
     */
    private static void unitConversion() {
        System.out.println("1. 元分转换");

        // 元转分
        BigDecimal yuan1 = new BigDecimal("10.50");
        Long fen1 = MoneyUtils.yuan2Fen(yuan1);
        System.out.println(yuan1 + " 元 = " + fen1 + " 分");

        // 分转元
        Long fen2 = 2580L;
        BigDecimal yuan2 = MoneyUtils.fen2Yuan(fen2);
        System.out.println(fen2 + " 分 = " + yuan2 + " 元");

        // 往返转换
        BigDecimal original = new BigDecimal("123.45");
        Long toFen = MoneyUtils.yuan2Fen(original);
        BigDecimal backToYuan = MoneyUtils.fen2Yuan(toFen);
        System.out.println("往返转换: " + original + " -> " + toFen + " -> " + backToYuan);
        System.out.println();
    }

    /**
     * 2. 基础运算
     */
    private static void basicOperations() {
        System.out.println("2. 基础运算（避免精度丢失）");

        BigDecimal price1 = new BigDecimal("10.50");
        BigDecimal price2 = new BigDecimal("20.30");

        // 相加
        BigDecimal sum = MoneyUtils.add(price1, price2);
        System.out.println(price1 + " + " + price2 + " = " + sum);

        // 多个金额相加
        BigDecimal total = MoneyUtils.add(
                new BigDecimal("10.50"),
                new BigDecimal("20.30"),
                new BigDecimal("5.00"),
                new BigDecimal("3.20")
        );
        System.out.println("多个金额相加: " + total);

        // 相减
        BigDecimal diff = MoneyUtils.subtract(new BigDecimal("30.50"), new BigDecimal("10.30"));
        System.out.println("30.50 - 10.30 = " + diff);

        // 相乘
        BigDecimal product = MoneyUtils.multiply(new BigDecimal("10.50"), new BigDecimal("2"));
        System.out.println("10.50 × 2 = " + product);

        // 相除
        BigDecimal quotient = MoneyUtils.divide(new BigDecimal("10.00"), new BigDecimal("3"));
        System.out.println("10.00 ÷ 3 = " + quotient + " (四舍五入到2位小数)");

        // 演示精度问题
        System.out.println("\n演示浮点数精度问题:");
        double d1 = 10.50;
        double d2 = 20.30;
        System.out.println("double 直接相加: " + d1 + " + " + d2 + " = " + (d1 + d2) + " ❌");
        System.out.println("BigDecimal 相加: " + price1 + " + " + price2 + " = " + sum + " ✓");
        System.out.println();
    }

    /**
     * 3. 金额比较
     */
    private static void comparison() {
        System.out.println("3. 金额比较");

        BigDecimal money1 = new BigDecimal("10.50");
        BigDecimal money2 = new BigDecimal("20.30");
        BigDecimal money3 = new BigDecimal("10.50");

        // 相等
        System.out.println(money1 + " equals " + money3 + ": " + MoneyUtils.equals(money1, money3));

        // 大于
        System.out.println(money2 + " > " + money1 + ": " + MoneyUtils.greaterThan(money2, money1));

        // 小于
        System.out.println(money1 + " < " + money2 + ": " + MoneyUtils.lessThan(money1, money2));

        // 比较
        int compareResult = MoneyUtils.compare(money1, money2);
        System.out.println("compare(" + money1 + ", " + money2 + ") = " + compareResult + " (负数表示小于)");

        // 状态判断
        System.out.println("\n金额状态判断:");
        System.out.println(BigDecimal.ZERO + " 是否为零: " + MoneyUtils.isZero(BigDecimal.ZERO));
        System.out.println(new BigDecimal("10.50") + " 是否为正数: " + MoneyUtils.isPositive(new BigDecimal("10.50")));
        System.out.println(new BigDecimal("-5.00") + " 是否为负数: " + MoneyUtils.isNegative(new BigDecimal("-5.00")));
        System.out.println();
    }

    /**
     * 4. 格式化显示
     */
    private static void formatting() {
        System.out.println("4. 格式化显示");

        BigDecimal amount1 = new BigDecimal("12345.67");
        BigDecimal amount2 = new BigDecimal("1234567.89");

        // 标准格式（千分位）
        System.out.println("标准格式: " + MoneyUtils.format(amount1));
        System.out.println("大金额: " + MoneyUtils.format(amount2));

        // 带货币符号
        System.out.println("人民币: " + MoneyUtils.formatWithSymbol(amount1));
        System.out.println("美元: " + MoneyUtils.formatWithSymbol(amount1, "$"));
        System.out.println("欧元: " + MoneyUtils.formatWithSymbol(amount1, "€"));

        // 自定义格式
        System.out.println("自定义格式: " + MoneyUtils.format(amount1, "#,##0.00元"));
        System.out.println();
    }

    /**
     * 5. 其他工具方法
     */
    private static void otherMethods() {
        System.out.println("5. 其他工具方法");

        BigDecimal value1 = new BigDecimal("10.50");
        BigDecimal value2 = new BigDecimal("20.30");

        // 最大值/最小值
        System.out.println("max(" + value1 + ", " + value2 + ") = " + MoneyUtils.max(value1, value2));
        System.out.println("min(" + value1 + ", " + value2 + ") = " + MoneyUtils.min(value1, value2));

        // 绝对值
        BigDecimal negative = new BigDecimal("-15.50");
        System.out.println("abs(" + negative + ") = " + MoneyUtils.abs(negative));

        // 相反数
        BigDecimal positive = new BigDecimal("20.00");
        System.out.println("negate(" + positive + ") = " + MoneyUtils.negate(positive));

        // 设置小数位数
        BigDecimal value = new BigDecimal("10.556");
        System.out.println("setScale(" + value + ", 2) = " + MoneyUtils.setScale(value, 2));
        System.out.println();
    }

    /**
     * 6. 实际应用场景
     */
    private static void practicalUseCases() {
        System.out.println("6. 实际应用场景");

        // 场景 1: 购物车结算
        System.out.println("场景 1: 购物车结算");
        shoppingCartCheckout();

        System.out.println();

        // 场景 2: 订单折扣计算
        System.out.println("场景 2: 订单折扣计算");
        orderDiscount();

        System.out.println();

        // 场景 3: 商品利润计算
        System.out.println("场景 3: 商品利润计算");
        profitCalculation();

        System.out.println();

        // 场景 4: 账单分摊
        System.out.println("场景 4: 账单分摊");
        billSharing();
    }

    /**
     * 场景 1: 购物车结算
     */
    private static void shoppingCartCheckout() {
        List<CartItem> items = Arrays.asList(
                new CartItem("商品A", new BigDecimal("99.90"), 2),
                new CartItem("商品B", new BigDecimal("159.00"), 1),
                new CartItem("商品C", new BigDecimal("49.50"), 3)
        );

        System.out.println("购物车商品:");
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : items) {
            BigDecimal itemTotal = MoneyUtils.multiply(item.price, new BigDecimal(item.quantity));
            totalAmount = MoneyUtils.add(totalAmount, itemTotal);
            System.out.println("  " + item.name + ": " +
                    MoneyUtils.formatWithSymbol(item.price) + " × " + item.quantity +
                    " = " + MoneyUtils.formatWithSymbol(itemTotal));
        }

        System.out.println("总金额: " + MoneyUtils.formatWithSymbol(totalAmount));
        System.out.println("总金额（分）: " + MoneyUtils.yuan2Fen(totalAmount) + " 分");
    }

    /**
     * 场景 2: 订单折扣计算
     */
    private static void orderDiscount() {
        BigDecimal originalPrice = new BigDecimal("299.00");
        BigDecimal discountRate = new BigDecimal("0.85"); // 85折

        BigDecimal discountedPrice = MoneyUtils.multiply(originalPrice, discountRate);
        BigDecimal saved = MoneyUtils.subtract(originalPrice, discountedPrice);

        System.out.println("原价: " + MoneyUtils.formatWithSymbol(originalPrice));
        System.out.println("折扣: 85折");
        System.out.println("折后价: " + MoneyUtils.formatWithSymbol(discountedPrice));
        System.out.println("节省: " + MoneyUtils.formatWithSymbol(saved));

        // 判断是否满足优惠条件
        BigDecimal threshold = new BigDecimal("200.00");
        if (MoneyUtils.greaterThanOrEqual(discountedPrice, threshold)) {
            System.out.println("✓ 满足满200减30活动条件");
        }
    }

    /**
     * 场景 3: 商品利润计算
     */
    private static void profitCalculation() {
        BigDecimal cost = new BigDecimal("120.00");      // 成本
        BigDecimal sellingPrice = new BigDecimal("199.00"); // 售价

        BigDecimal profit = MoneyUtils.subtract(sellingPrice, cost);
        BigDecimal profitRate = MoneyUtils.divide(profit, cost, 4, null);
        BigDecimal profitRatePercent = MoneyUtils.multiply(profitRate, new BigDecimal("100"));

        System.out.println("成本: " + MoneyUtils.formatWithSymbol(cost));
        System.out.println("售价: " + MoneyUtils.formatWithSymbol(sellingPrice));
        System.out.println("利润: " + MoneyUtils.formatWithSymbol(profit));
        System.out.println("利润率: " + profitRatePercent.setScale(2) + "%");
    }

    /**
     * 场景 4: 账单分摊
     */
    private static void billSharing() {
        BigDecimal totalBill = new BigDecimal("358.50");
        int numberOfPeople = 3;

        BigDecimal perPerson = MoneyUtils.divide(totalBill, new BigDecimal(numberOfPeople));

        System.out.println("总账单: " + MoneyUtils.formatWithSymbol(totalBill));
        System.out.println("人数: " + numberOfPeople);
        System.out.println("每人应付: " + MoneyUtils.formatWithSymbol(perPerson));

        // 验证
        BigDecimal totalCheck = MoneyUtils.multiply(perPerson, new BigDecimal(numberOfPeople));
        System.out.println("验证: " + perPerson + " × " + numberOfPeople + " = " + totalCheck);
    }

    // ==================== 测试数据类 ====================

    static class CartItem {
        String name;
        BigDecimal price;
        int quantity;

        CartItem(String name, BigDecimal price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
    }
}
