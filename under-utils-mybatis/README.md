# Under-Utils MyBatis

MyBatis-Plus support for audit fields, logical delete conventions, safe pagination, and sort-field whitelisting.

The module assumes the application already uses MyBatis-Plus. It does not hide MyBatis-Plus; it adds a small set of project-level defaults and safer request-facing pagination APIs.

## Dependency

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-mybatis</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Main APIs

| API | Purpose |
|-----|---------|
| `BaseEntity` | Common `id`, create/update time, create/update user, and logical delete fields. |
| `DefaultMetaObjectHandler` | Fills audit fields during insert and update. |
| `AuditorProvider` | Supplies the current audit user id. |
| `SafePageQuery` | Request-facing pagination model that avoids raw database columns. |
| `SortFieldMapping` | Maps public sort fields to allowed database columns. |
| `PageResult` | Small response wrapper around MyBatis-Plus pages. |
| `MybatisPlusConfig` | Factory for common MyBatis-Plus interceptor setup. |

## Basic Setup

```java
@Configuration
public class MybatisConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return MybatisPlusConfig.mybatisPlusInterceptor(DbType.MYSQL);
    }

    @Bean
    public AuditorProvider auditorProvider() {
        return () -> 1001L;
    }

    @Bean
    public DefaultMetaObjectHandler metaObjectHandler(AuditorProvider auditorProvider) {
        return new DefaultMetaObjectHandler(auditorProvider);
    }
}
```

Typical MyBatis-Plus logical delete configuration:

```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

## Entity

```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private String username;
    private String email;
    private Integer status;
}
```

Matching table fields:

```sql
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(100),
  status INT DEFAULT 1,
  create_time DATETIME,
  update_time DATETIME,
  create_by BIGINT,
  update_by BIGINT,
  deleted INT DEFAULT 0
);
```

## Safe Pagination

Expose public sort names instead of database column names:

```java
SortFieldMapping mapping = SortFieldMapping.builder()
        .add("createdAt", "create_time")
        .add("username", "username")
        .add("status", "status")
        .build();

SafePageQuery pageQuery = SafePageQuery.of(1L, 20L)
        .orderByDesc("createdAt");

IPage<SysUser> page = userMapper.selectPage(
        pageQuery.buildPage(mapping),
        new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getStatus, 1)
);

PageResult<SysUser> result = PageResult.of(page);
```

`SafePageQuery` ignores sort fields that are not present in `SortFieldMapping`. This prevents raw request values from reaching `ORDER BY`.

`PageQuery` remains for compatibility but is not recommended for request input.

## Audit Fields

`DefaultMetaObjectHandler` fills:

- `createTime` and `createBy` on insert.
- `updateTime` and `updateBy` on insert and update.
- `deleted` on insert when it is null.

Provide an `AuditorProvider` from your security context:

```java
@Bean
public AuditorProvider auditorProvider() {
    return () -> {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof YourUserDetails user)) {
            return null;
        }
        return user.getId();
    };
}
```

## Integration Tests

MySQL-backed integration coverage lives in `under-utils-test` and runs through Testcontainers:

```bash
mvn -Pintegration-tests -pl under-utils-test -am test -Dtest=MybatisIntegrationTest
```

The default Maven build does not start MySQL or Docker.

## Notes

- Keep `deleted` aligned with the configured MyBatis-Plus logical delete field.
- Prefer `SafePageQuery` for web/API input.
- Keep database-specific behavior in application code or a dedicated module; this module only provides reusable MyBatis-Plus helpers.
