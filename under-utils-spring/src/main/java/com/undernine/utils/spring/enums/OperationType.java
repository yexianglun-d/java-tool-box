package com.undernine.utils.spring.enums;

/**
 * 操作类型枚举
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public enum OperationType {
    
    /**
     * 查询
     */
    QUERY("查询"),
    
    /**
     * 新增
     */
    CREATE("新增"),
    
    /**
     * 修改
     */
    UPDATE("修改"),
    
    /**
     * 删除
     */
    DELETE("删除"),
    
    /**
     * 导出
     */
    EXPORT("导出"),
    
    /**
     * 导入
     */
    IMPORT("导入"),
    
    /**
     * 登录
     */
    LOGIN("登录"),
    
    /**
     * 登出
     */
    LOGOUT("登出"),
    
    /**
     * 其他
     */
    OTHER("其他");
    
    private final String description;
    
    OperationType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
