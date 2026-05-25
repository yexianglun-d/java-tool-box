package com.undernine.utils.mybatis;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.undernine.utils.mybatis.page.SafePageQuery;
import com.undernine.utils.mybatis.page.SortFieldMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 安全分页查询测试
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@DisplayName("安全分页查询测试")
class SafePageQueryTest {

    @Test
    @DisplayName("测试安全排序映射")
    void testSafeSortMapping() {
        SortFieldMapping mapping = SortFieldMapping.builder()
                .add("createdAt", "create_time")
                .add("username", "user_name")
                .build();

        SafePageQuery query = SafePageQuery.of(2L, 20L)
                .orderByDesc("createdAt")
                .orderByAsc("username");

        Page<Object> page = query.buildPage(mapping);

        assertThat(page.getCurrent()).isEqualTo(2L);
        assertThat(page.getSize()).isEqualTo(20L);
        assertThat(page.orders()).hasSize(2);
        assertThat(page.orders().get(0).getColumn()).isEqualTo("create_time");
        assertThat(page.orders().get(0).isAsc()).isFalse();
        assertThat(page.orders().get(1).getColumn()).isEqualTo("user_name");
        assertThat(page.orders().get(1).isAsc()).isTrue();
    }

    @Test
    @DisplayName("测试非法排序字段拒绝")
    void testRejectIllegalSortField() {
        SortFieldMapping mapping = SortFieldMapping.builder()
                .add("createdAt", "create_time")
                .build();

        SafePageQuery query = SafePageQuery.of()
                .orderByDesc("id desc; drop table user");

        assertThatThrownBy(() -> query.buildPage(mapping))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported sort field");
    }

    @Test
    @DisplayName("测试默认排序")
    void testDefaultSort() {
        SortFieldMapping mapping = SortFieldMapping.of(Map.of(
                "id", "id",
                "createdAt", "create_time"
        ));

        SafePageQuery query = SafePageQuery.of(1L, 10L);

        Page<Object> page = query.buildPage(mapping, SafePageQuery.SortOrder.desc("createdAt"));

        assertThat(page.orders()).hasSize(1);
        assertThat(page.orders().get(0).getColumn()).isEqualTo("create_time");
        assertThat(page.orders().get(0).isAsc()).isFalse();
    }

    @Test
    @DisplayName("测试请求排序优先于默认排序")
    void testRequestSortOverridesDefaultSort() {
        SortFieldMapping mapping = SortFieldMapping.of(Map.of(
                "id", "id",
                "createdAt", "create_time"
        ));

        SafePageQuery query = SafePageQuery.of()
                .orderByAsc("id");

        Page<Object> page = query.buildPage(mapping, SafePageQuery.SortOrder.desc("createdAt"));

        assertThat(page.orders()).hasSize(1);
        assertThat(page.orders().get(0).getColumn()).isEqualTo("id");
        assertThat(page.orders().get(0).isAsc()).isTrue();
    }

    @Test
    @DisplayName("测试映射拒绝不安全数据库列名")
    void testRejectUnsafeMappingColumn() {
        assertThatThrownBy(() -> SortFieldMapping.builder()
                .add("createdAt", "create_time desc")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsafe sort column");
    }

    @Test
    @DisplayName("测试分页参数兜底和最大分页限制")
    void testPageBounds() {
        SafePageQuery query = SafePageQuery.of(0L, 2001L);

        Page<Object> page = query.buildPage(SortFieldMapping.builder().build());

        assertThat(page.getCurrent()).isEqualTo(1L);
        assertThat(page.getSize()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("测试排序字段数量上限")
    void testRejectTooManySortOrders() {
        SafePageQuery query = SafePageQuery.of()
                .orderByAsc("id")
                .orderByAsc("createdAt")
                .orderByAsc("updatedAt")
                .orderByAsc("username")
                .orderByAsc("status");

        assertThatThrownBy(() -> query.orderByAsc("tenantId"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sort order count must not exceed");
    }

    @Test
    @DisplayName("测试反序列化排序字段数量上限")
    void testRejectTooManyDeserializedSortOrders() {
        SortFieldMapping mapping = SortFieldMapping.of(Map.of(
                "id", "id",
                "createdAt", "create_time",
                "updatedAt", "update_time",
                "username", "user_name",
                "status", "status",
                "tenantId", "tenant_id"
        ));
        SafePageQuery query = SafePageQuery.of();
        query.setOrders(List.of(
                SafePageQuery.SortOrder.asc("id"),
                SafePageQuery.SortOrder.asc("createdAt"),
                SafePageQuery.SortOrder.asc("updatedAt"),
                SafePageQuery.SortOrder.asc("username"),
                SafePageQuery.SortOrder.asc("status"),
                SafePageQuery.SortOrder.asc("tenantId")
        ));

        assertThatThrownBy(() -> query.buildPage(mapping))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sort order count must not exceed");
    }
}
