package com.undernine.utils.spring.enums;

/**
 * 敏感信息类型枚举
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public enum SensitiveType {
    
    /**
     * 手机号：138****5678
     */
    MOBILE_PHONE,
    
    /**
     * 身份证号：320***********1234
     */
    ID_CARD,
    
    /**
     * 银行卡号：6222 **** **** 1234
     */
    BANK_CARD,
    
    /**
     * 邮箱：a***@example.com
     */
    EMAIL,
    
    /**
     * 姓名：张*
     */
    CHINESE_NAME,
    
    /**
     * 地址：北京市海淀区******
     */
    ADDRESS,
    
    /**
     * 密码：*** (完全隐藏)
     */
    PASSWORD,
    
    /**
     * 固定电话：010-****5678
     */
    FIXED_PHONE,
    
    /**
     * 车牌号：京A·****1
     */
    CAR_LICENSE,
    
    /**
     * 自定义规则
     */
    CUSTOM
}
