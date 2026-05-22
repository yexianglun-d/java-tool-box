package com.undernine.utils.mybatis.page;

import com.baomidou.mybatisplus.core.metadata.OrderItem;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 排序字段白名单映射。
 * <p>
 * 维护前端可见字段名到数据库列名的映射，避免分页接口直接接收数据库列名拼接到 ORDER BY。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SortFieldMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Pattern SAFE_COLUMN_PATTERN =
            Pattern.compile("[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)?");

    private final Map<String, String> mappings;

    private SortFieldMapping(Map<String, String> mappings) {
        this.mappings = Collections.unmodifiableMap(new LinkedHashMap<>(mappings));
    }

    /**
     * 创建字段映射。
     *
     * @param mappings 前端字段名到数据库列名的映射
     * @return 字段映射
     */
    public static SortFieldMapping of(Map<String, String> mappings) {
        Objects.requireNonNull(mappings, "mappings must not be null");
        Builder builder = builder();
        mappings.forEach(builder::add);
        return builder.build();
    }

    /**
     * 创建字段映射构建器。
     *
     * @return 构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 是否允许指定前端字段排序。
     *
     * @param field 前端字段名
     * @return true 表示字段已在白名单中
     */
    public boolean contains(String field) {
        return mappings.containsKey(field);
    }

    /**
     * 将前端字段名解析为数据库列名。
     *
     * @param field 前端字段名
     * @return 数据库列名
     * @throws IllegalArgumentException 字段不在白名单中时抛出
     */
    public String toColumn(String field) {
        String column = mappings.get(field);
        if (column == null) {
            throw new IllegalArgumentException("Unsupported sort field: " + field);
        }
        return column;
    }

    /**
     * 创建升序排序项。
     *
     * @param field 前端字段名
     * @return MyBatis-Plus 排序项
     */
    public OrderItem asc(String field) {
        return OrderItem.asc(toColumn(field));
    }

    /**
     * 创建降序排序项。
     *
     * @param field 前端字段名
     * @return MyBatis-Plus 排序项
     */
    public OrderItem desc(String field) {
        return OrderItem.desc(toColumn(field));
    }

    /**
     * 返回不可变映射快照。
     *
     * @return 前端字段名到数据库列名的映射
     */
    public Map<String, String> asMap() {
        return mappings;
    }

    /**
     * 排序字段映射构建器。
     */
    public static final class Builder {

        private final Map<String, String> mappings = new LinkedHashMap<>();

        private Builder() {
        }

        /**
         * 添加字段映射。
         *
         * @param field  前端字段名
         * @param column 数据库列名
         * @return this
         */
        public Builder add(String field, String column) {
            validateField(field);
            validateColumn(column);
            mappings.put(field, column);
            return this;
        }

        /**
         * 构建字段映射。
         *
         * @return 字段映射
         */
        public SortFieldMapping build() {
            return new SortFieldMapping(mappings);
        }

        private static void validateField(String field) {
            if (field == null || field.trim().isEmpty()) {
                throw new IllegalArgumentException("sort field must not be blank");
            }
        }

        private static void validateColumn(String column) {
            if (column == null || column.trim().isEmpty()) {
                throw new IllegalArgumentException("sort column must not be blank");
            }
            if (!SAFE_COLUMN_PATTERN.matcher(column).matches()) {
                throw new IllegalArgumentException("Unsafe sort column: " + column);
            }
        }
    }
}
