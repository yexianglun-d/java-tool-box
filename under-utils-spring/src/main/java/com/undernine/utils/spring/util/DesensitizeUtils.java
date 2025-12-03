package com.undernine.utils.spring.util;

import com.undernine.utils.spring.enums.SensitiveType;

/**
 * 敏感信息脱敏工具类
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class DesensitizeUtils {

    private static final String MASK_CHAR = "*";

    /**
     * 根据类型脱敏
     *
     * @param value 原始值
     * @param type  脱敏类型
     * @return 脱敏后的值
     */
    public static String desensitize(String value, SensitiveType type) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        return switch (type) {
            case MOBILE_PHONE -> mobilePhone(value);
            case ID_CARD -> idCard(value);
            case BANK_CARD -> bankCard(value);
            case EMAIL -> email(value);
            case CHINESE_NAME -> chineseName(value);
            case ADDRESS -> address(value);
            case PASSWORD -> password(value);
            case FIXED_PHONE -> fixedPhone(value);
            case CAR_LICENSE -> carLicense(value);
            default -> value;
        };
    }

    /**
     * 自定义规则脱敏
     *
     * @param value      原始值
     * @param customRule 规则，格式："前保留位数,后保留位数"
     * @return 脱敏后的值
     */
    public static String desensitizeCustom(String value, String customRule) {
        if (value == null || value.isEmpty() || customRule == null || customRule.isEmpty()) {
            return value;
        }

        String[] parts = customRule.split(",");
        if (parts.length != 2) {
            return value;
        }

        try {
            int prefixLen = Integer.parseInt(parts[0].trim());
            int suffixLen = Integer.parseInt(parts[1].trim());
            return desensitize(value, prefixLen, suffixLen);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    /**
     * 手机号脱敏：138****5678
     */
    public static String mobilePhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return desensitize(phone, 3, 4);
    }

    /**
     * 身份证号脱敏：320***********1234
     */
    public static String idCard(String idCard) {
        if (idCard == null || (idCard.length() != 15 && idCard.length() != 18)) {
            return idCard;
        }
        return desensitize(idCard, 3, 4);
    }

    /**
     * 银行卡号脱敏：6222 **** **** 1234
     */
    public static String bankCard(String cardNo) {
        if (cardNo == null || cardNo.length() < 8) {
            return cardNo;
        }
        return desensitize(cardNo, 4, 4);
    }

    /**
     * 邮箱脱敏：a***@example.com
     */
    public static String email(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int index = email.indexOf("@");
        String prefix = email.substring(0, index);
        String suffix = email.substring(index);
        
        if (prefix.length() <= 1) {
            return email;
        }
        
        return prefix.charAt(0) + MASK_CHAR.repeat(Math.min(prefix.length() - 1, 3)) + suffix;
    }

    /**
     * 中文姓名脱敏：张*、欧阳**
     */
    public static String chineseName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        int length = name.length();
        if (length == 1) {
            return name;
        }
        return name.charAt(0) + MASK_CHAR.repeat(length - 1);
    }

    /**
     * 地址脱敏：北京市海淀区******
     */
    public static String address(String address) {
        if (address == null || address.length() <= 6) {
            return address;
        }
        return desensitize(address, 6, 0);
    }

    /**
     * 密码完全脱敏：***
     */
    public static String password(String password) {
        if (password == null) {
            return null;
        }
        return MASK_CHAR.repeat(3);
    }

    /**
     * 固定电话脱敏：010-****5678
     */
    public static String fixedPhone(String phone) {
        if (phone == null) {
            return null;
        }
        if (phone.contains("-")) {
            String[] parts = phone.split("-");
            if (parts.length == 2 && parts[1].length() >= 4) {
                return parts[0] + "-" + desensitize(parts[1], 0, 4);
            }
        }
        return desensitize(phone, 3, 4);
    }

    /**
     * 车牌号脱敏：京A·****1
     */
    public static String carLicense(String license) {
        if (license == null || license.length() < 7) {
            return license;
        }
        return desensitize(license, 3, 1);
    }

    /**
     * 通用脱敏方法
     *
     * @param value     原始值
     * @param prefixLen 前缀保留长度
     * @param suffixLen 后缀保留长度
     * @return 脱敏后的值
     */
    private static String desensitize(String value, int prefixLen, int suffixLen) {
        if (value == null) {
            return null;
        }

        int length = value.length();
        if (length <= prefixLen + suffixLen) {
            return value;
        }

        String prefix = value.substring(0, prefixLen);
        String suffix = value.substring(length - suffixLen);
        int maskLength = length - prefixLen - suffixLen;

        return prefix + MASK_CHAR.repeat(maskLength) + suffix;
    }
}
