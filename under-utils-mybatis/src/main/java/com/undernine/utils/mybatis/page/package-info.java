/**
 * MyBatis-Plus 分页功能封装
 * <p>
 * 提供统一的分页查询参数、排序字段白名单映射和返回结果封装，简化分页操作。
 * </p>
 *
 * <h3>核心类：</h3>
 * <ul>
 *     <li>{@link com.undernine.utils.mybatis.page.SafePageQuery} - 面向外部请求的白名单排序分页查询参数封装</li>
 *     <li>{@link com.undernine.utils.mybatis.page.SortFieldMapping} - 前端排序字段到数据库列名的白名单映射</li>
 *     <li>{@link com.undernine.utils.mybatis.page.PageResult} - 分页查询结果封装</li>
 *     <li>{@link com.undernine.utils.mybatis.page.PageQuery} - 兼容保留的内部可信排序分页参数</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. Controller 接收分页参数
 * @GetMapping("/users")
 * public PageResult<UserVO> pageUsers(SafePageQuery pageQuery, String keyword) {
 *     return userService.pageUsers(pageQuery, keyword);
 * }
 *
 * // 2. Service 执行分页查询
 * public PageResult<UserVO> pageUsers(SafePageQuery pageQuery, String keyword) {
 *     SortFieldMapping mapping = SortFieldMapping.builder()
 *         .add("createdAt", "create_time")
 *         .add("username", "username")
 *         .build();
 *     LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
 *         .like(StringUtils.hasText(keyword), User::getUsername, keyword);
 *
 *     IPage<User> page = userMapper.selectPage(pageQuery.buildPage(mapping), wrapper);
 *
 *     // 转换为 VO
 *     List<UserVO> voList = page.getRecords().stream()
 *         .map(this::convertToVO)
 *         .collect(Collectors.toList());
 *
 *     return PageResult.of(page, voList);
 * }
 *
 * // 3. 前端请求示例
 * // GET /users?current=1&size=10&orders[0].field=createdAt&orders[0].asc=false&keyword=admin
 *
 * // 4. 返回结果示例
 * // {
 * //   "records": [...],
 * //   "total": 100,
 * //   "current": 1,
 * //   "size": 10,
 * //   "pages": 10
 * // }
 * }</pre>
 *
 * <h3>功能特性：</h3>
 * <ul>
 *     <li>支持自定义当前页和每页大小</li>
 *     <li>支持基于白名单映射的单字段或多字段排序（升序/降序）</li>
 *     <li>支持最大分页限制（默认 1000 条）</li>
 *     <li>支持数据类型转换（Entity -> VO）</li>
 *     <li>支持创建空分页结果</li>
 * </ul>
 *
 * <h3>注意事项：</h3>
 * <ul>
 *     <li>分页参数 current 从 1 开始，不是 0</li>
 *     <li>单页最大数量默认为 1000</li>
 *     <li>{@link com.undernine.utils.mybatis.page.PageQuery} 面向内部可信排序字段，排序字段名使用数据库字段名，不建议作为 Web 入参</li>
 *     <li>{@link com.undernine.utils.mybatis.page.SafePageQuery} 面向外部请求排序字段，必须通过
 *     {@link com.undernine.utils.mybatis.page.SortFieldMapping} 白名单映射为数据库列名</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.mybatis.page;
