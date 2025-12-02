package com.undernine.utils.mybatis.page;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页查询参数
 * <p>
 * 统一的分页查询参数封装
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 默认页码
     */
    private static final long DEFAULT_CURRENT = 1L;

    /**
     * 默认每页大小
     */
    private static final long DEFAULT_SIZE = 10L;

    /**
     * 最大每页大小
     */
    private static final long MAX_SIZE = 1000L;

    /**
     * 当前页码（从 1 开始）
     */
    private Long current = DEFAULT_CURRENT;

    /**
     * 每页显示条数
     */
    private Long size = DEFAULT_SIZE;

    /**
     * 排序字段列表
     */
    private List<OrderItem> orders;

    /**
     * 构建 MyBatis-Plus Page 对象
     *
     * @param <T> 实体类型
     * @return Page 对象
     */
    public <T> Page<T> buildPage() {
        return buildPage(null);
    }

    /**
     * 构建 MyBatis-Plus Page 对象（带默认排序）
     *
     * @param defaultOrders 默认排序（当未指定排序时使用）
     * @param <T>           实体类型
     * @return Page 对象
     */
    public <T> Page<T> buildPage(List<OrderItem> defaultOrders) {
        // 校验分页参数
        long validCurrent = this.current != null && this.current > 0 ? this.current : DEFAULT_CURRENT;
        long validSize = this.size != null && this.size > 0 ? this.size : DEFAULT_SIZE;

        // 限制最大分页大小
        if (validSize > MAX_SIZE) {
            validSize = MAX_SIZE;
        }

        Page<T> page = new Page<>(validCurrent, validSize);

        // 设置排序
        if (this.orders != null && !this.orders.isEmpty()) {
            page.setOrders(this.orders);
        } else if (defaultOrders != null && !defaultOrders.isEmpty()) {
            page.setOrders(defaultOrders);
        }

        return page;
    }

    /**
     * 添加升序排序字段
     *
     * @param column 字段名
     * @return this
     */
    public PageQuery orderByAsc(String column) {
        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }
        this.orders.add(OrderItem.asc(column));
        return this;
    }

    /**
     * 添加降序排序字段
     *
     * @param column 字段名
     * @return this
     */
    public PageQuery orderByDesc(String column) {
        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }
        this.orders.add(OrderItem.desc(column));
        return this;
    }

    /**
     * 创建默认分页查询参数
     *
     * @return PageQuery
     */
    public static PageQuery of() {
        return new PageQuery();
    }

    /**
     * 创建分页查询参数
     *
     * @param current 当前页码
     * @param size    每页显示条数
     * @return PageQuery
     */
    public static PageQuery of(Long current, Long size) {
        PageQuery query = new PageQuery();
        query.setCurrent(current);
        query.setSize(size);
        return query;
    }
}
