package com.undernine.utils.mybatis.page;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 安全分页查询参数。
 * <p>
 * 排序字段使用前端字段名，并在构建 Page 时通过 {@link SortFieldMapping} 白名单映射为数据库列名。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class SafePageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final long DEFAULT_CURRENT = 1L;
    private static final long DEFAULT_SIZE = 10L;
    private static final long MAX_SIZE = 1000L;

    /**
     * 当前页码（从 1 开始）。
     */
    private Long current = DEFAULT_CURRENT;

    /**
     * 每页显示条数。
     */
    private Long size = DEFAULT_SIZE;

    /**
     * 前端排序字段列表。
     */
    private List<SortOrder> orders;

    /**
     * 构建 MyBatis-Plus Page 对象。
     *
     * @param mapping 排序字段白名单映射
     * @param <T>     实体类型
     * @return Page 对象
     */
    public <T> Page<T> buildPage(SortFieldMapping mapping) {
        return buildPage(mapping, Collections.emptyList());
    }

    /**
     * 构建 MyBatis-Plus Page 对象（带默认排序）。
     *
     * @param mapping       排序字段白名单映射
     * @param defaultOrders 默认排序，当前请求未指定排序时使用
     * @param <T>           实体类型
     * @return Page 对象
     */
    public <T> Page<T> buildPage(SortFieldMapping mapping, List<SortOrder> defaultOrders) {
        Objects.requireNonNull(mapping, "mapping must not be null");

        long validCurrent = this.current != null && this.current > 0 ? this.current : DEFAULT_CURRENT;
        long validSize = this.size != null && this.size > 0 ? this.size : DEFAULT_SIZE;
        if (validSize > MAX_SIZE) {
            validSize = MAX_SIZE;
        }

        Page<T> page = new Page<>(validCurrent, validSize);
        List<SortOrder> effectiveOrders = hasOrders(this.orders) ? this.orders : defaultOrders;
        if (hasOrders(effectiveOrders)) {
            page.setOrders(toOrderItems(mapping, effectiveOrders));
        }
        return page;
    }

    /**
     * 构建 MyBatis-Plus Page 对象（带默认排序）。
     *
     * @param mapping       排序字段白名单映射
     * @param defaultOrders 默认排序，当前请求未指定排序时使用
     * @param <T>           实体类型
     * @return Page 对象
     */
    public <T> Page<T> buildPage(SortFieldMapping mapping, SortOrder... defaultOrders) {
        List<SortOrder> defaults = defaultOrders == null
                ? Collections.emptyList()
                : Arrays.asList(defaultOrders);
        return buildPage(mapping, defaults);
    }

    /**
     * 添加升序排序字段。
     *
     * @param field 前端字段名
     * @return this
     */
    public SafePageQuery orderByAsc(String field) {
        addOrder(SortOrder.asc(field));
        return this;
    }

    /**
     * 添加降序排序字段。
     *
     * @param field 前端字段名
     * @return this
     */
    public SafePageQuery orderByDesc(String field) {
        addOrder(SortOrder.desc(field));
        return this;
    }

    /**
     * 创建默认分页查询参数。
     *
     * @return SafePageQuery
     */
    public static SafePageQuery of() {
        return new SafePageQuery();
    }

    /**
     * 创建分页查询参数。
     *
     * @param current 当前页码
     * @param size    每页显示条数
     * @return SafePageQuery
     */
    public static SafePageQuery of(Long current, Long size) {
        SafePageQuery query = new SafePageQuery();
        query.setCurrent(current);
        query.setSize(size);
        return query;
    }

    private void addOrder(SortOrder order) {
        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }
        this.orders.add(order);
    }

    private static boolean hasOrders(List<SortOrder> orders) {
        return orders != null && !orders.isEmpty();
    }

    private static List<OrderItem> toOrderItems(SortFieldMapping mapping, List<SortOrder> orders) {
        return orders.stream()
                .map(order -> order.isAsc() ? mapping.asc(order.getField()) : mapping.desc(order.getField()))
                .collect(Collectors.toList());
    }

    /**
     * 前端排序字段。
     */
    @Data
    public static class SortOrder implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 前端字段名。
         */
        private String field;

        /**
         * 是否升序。
         */
        private boolean asc;

        public SortOrder() {
        }

        public SortOrder(String field, boolean asc) {
            this.field = field;
            this.asc = asc;
        }

        /**
         * 创建升序排序。
         *
         * @param field 前端字段名
         * @return 排序字段
         */
        public static SortOrder asc(String field) {
            return new SortOrder(field, true);
        }

        /**
         * 创建降序排序。
         *
         * @param field 前端字段名
         * @return 排序字段
         */
        public static SortOrder desc(String field) {
            return new SortOrder(field, false);
        }
    }
}
