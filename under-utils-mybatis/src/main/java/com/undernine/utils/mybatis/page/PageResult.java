package com.undernine.utils.mybatis.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装类
 * <p>
 * 统一的分页返回数据结构
 * </p>
 *
 * @param <T> 数据类型
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页显示条数
     */
    private Long size;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 从 MyBatis-Plus IPage 转换为 PageResult
     *
     * @param page MyBatis-Plus 分页对象
     * @param <T>  数据类型
     * @return PageResult
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setPages(page.getPages());
        return result;
    }

    /**
     * 从 MyBatis-Plus IPage 转换为 PageResult（带数据转换）
     *
     * @param page    MyBatis-Plus 分页对象
     * @param records 转换后的数据列表
     * @param <T>     数据类型
     * @return PageResult
     */
    public static <T> PageResult<T> of(IPage<?> page, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(page.getTotal());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setPages(page.getPages());
        return result;
    }

    /**
     * 创建空分页结果
     *
     * @param <T> 数据类型
     * @return 空分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(List.of(), 0L, 1L, 10L, 0L);
    }

    /**
     * 创建空分页结果（指定分页参数）
     *
     * @param current 当前页码
     * @param size    每页显示条数
     * @param <T>     数据类型
     * @return 空分页结果
     */
    public static <T> PageResult<T> empty(Long current, Long size) {
        return new PageResult<>(List.of(), 0L, current, size, 0L);
    }
}
