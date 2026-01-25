package com.undernine.utils.mybatis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.undernine.utils.mybatis.page.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PageResult 测试
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@DisplayName("PageResult 测试")
class PageResultTest {

    @Test
    @DisplayName("测试从 IPage 转换为 PageResult")
    void testOfFromIPage() {
        // 创建 MyBatis-Plus 分页对象
        IPage<String> page = new Page<>(2, 10);
        page.setRecords(Arrays.asList("item1", "item2", "item3"));
        page.setTotal(25);

        // 转换为 PageResult
        PageResult<String> result = PageResult.of(page);

        assertThat(result.getRecords()).hasSize(3);
        assertThat(result.getRecords()).containsExactly("item1", "item2", "item3");
        assertThat(result.getTotal()).isEqualTo(25L);
        assertThat(result.getCurrent()).isEqualTo(2L);
        assertThat(result.getSize()).isEqualTo(10L);
        assertThat(result.getPages()).isEqualTo(3L);
    }

    @Test
    @DisplayName("测试从 IPage 转换为 PageResult（带数据转换）")
    void testOfFromIPageWithConversion() {
        // 创建 MyBatis-Plus 分页对象
        IPage<Integer> page = new Page<>(1, 5);
        page.setRecords(Arrays.asList(1, 2, 3));
        page.setTotal(15);

        // 转换数据类型
        List<String> convertedRecords = Arrays.asList("1", "2", "3");
        PageResult<String> result = PageResult.of(page, convertedRecords);

        assertThat(result.getRecords()).hasSize(3);
        assertThat(result.getRecords()).containsExactly("1", "2", "3");
        assertThat(result.getTotal()).isEqualTo(15L);
        assertThat(result.getCurrent()).isEqualTo(1L);
        assertThat(result.getSize()).isEqualTo(5L);
        assertThat(result.getPages()).isEqualTo(3L);
    }

    @Test
    @DisplayName("测试创建空分页结果")
    void testEmpty() {
        PageResult<String> result = PageResult.empty();

        assertThat(result.getRecords()).isEmpty();
        assertThat(result.getTotal()).isEqualTo(0L);
        assertThat(result.getCurrent()).isEqualTo(1L);
        assertThat(result.getSize()).isEqualTo(10L);
        assertThat(result.getPages()).isEqualTo(0L);
    }

    @Test
    @DisplayName("测试创建空分页结果（指定分页参数）")
    void testEmptyWithParams() {
        PageResult<String> result = PageResult.empty(3L, 20L);

        assertThat(result.getRecords()).isEmpty();
        assertThat(result.getTotal()).isEqualTo(0L);
        assertThat(result.getCurrent()).isEqualTo(3L);
        assertThat(result.getSize()).isEqualTo(20L);
        assertThat(result.getPages()).isEqualTo(0L);
    }

    @Test
    @DisplayName("测试 PageResult 构造方法")
    void testConstructor() {
        List<String> records = Arrays.asList("a", "b", "c");
        PageResult<String> result = new PageResult<>(records, 100L, 5L, 20L, 5L);

        assertThat(result.getRecords()).hasSize(3);
        assertThat(result.getTotal()).isEqualTo(100L);
        assertThat(result.getCurrent()).isEqualTo(5L);
        assertThat(result.getSize()).isEqualTo(20L);
        assertThat(result.getPages()).isEqualTo(5L);
    }

    @Test
    @DisplayName("测试 PageResult Setter 方法")
    void testSetters() {
        PageResult<String> result = new PageResult<>();
        List<String> records = Arrays.asList("x", "y");

        result.setRecords(records);
        result.setTotal(50L);
        result.setCurrent(2L);
        result.setSize(25L);
        result.setPages(2L);

        assertThat(result.getRecords()).isEqualTo(records);
        assertThat(result.getTotal()).isEqualTo(50L);
        assertThat(result.getCurrent()).isEqualTo(2L);
        assertThat(result.getSize()).isEqualTo(25L);
        assertThat(result.getPages()).isEqualTo(2L);
    }

    @Test
    @DisplayName("测试 PageResult 序列化")
    void testSerializable() {
        PageResult<String> result = PageResult.empty();
        assertThat(result).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("测试第一页数据")
    void testFirstPage() {
        IPage<String> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList("item1", "item2"));
        page.setTotal(2);

        PageResult<String> result = PageResult.of(page);

        assertThat(result.getCurrent()).isEqualTo(1L);
        assertThat(result.getPages()).isEqualTo(1L);
        assertThat(result.getRecords()).hasSize(2);
    }

    @Test
    @DisplayName("测试最后一页数据")
    void testLastPage() {
        IPage<String> page = new Page<>(3, 10);
        page.setRecords(Arrays.asList("item21", "item22", "item23"));
        page.setTotal(23);

        PageResult<String> result = PageResult.of(page);

        assertThat(result.getCurrent()).isEqualTo(3L);
        assertThat(result.getPages()).isEqualTo(3L);
        assertThat(result.getRecords()).hasSize(3);
    }
}
