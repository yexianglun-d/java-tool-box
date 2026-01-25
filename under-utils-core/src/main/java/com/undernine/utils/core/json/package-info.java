/**
 * JSON 工具包
 * <p>
 * 提供基于 Jackson 的 JSON 序列化和反序列化工具类，支持对象、集合、泛型等多种类型。
 * </p>
 *
 * <h2>主要工具类</h2>
 * <ul>
 *   <li>{@link com.undernine.utils.core.json.JsonUtils} - JSON 工具类</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 对象转 JSON 字符串
 * User user = new User("张三", 25);
 * String json = JsonUtils.toJson(user);
 *
 * // JSON 字符串转对象
 * User parsedUser = JsonUtils.parseObject(json, User.class);
 *
 * // JSON 字符串转 List
 * String listJson = "[{\"name\":\"张三\",\"age\":25},{\"name\":\"李四\",\"age\":30}]";
 * List<User> users = JsonUtils.parseList(listJson, User.class);
 *
 * // JSON 字符串转 Map
 * String mapJson = "{\"name\":\"张三\",\"age\":25}";
 * Map<String, Object> map = JsonUtils.parseMap(mapJson);
 *
 * // 格式化 JSON（美化输出）
 * String prettyJson = JsonUtils.toPrettyJson(user);
 * }</pre>
 *
 * <h2>特性</h2>
 * <ul>
 *   <li>基于 Jackson 2.x，性能优异</li>
 *   <li>支持 Java 8 时间类型（LocalDateTime、LocalDate 等）</li>
 *   <li>支持泛型类型的序列化和反序列化</li>
 *   <li>提供安全的解析方法，解析失败返回 null 而不抛异常</li>
 *   <li>支持 JSON 格式化（美化输出）</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>依赖 Jackson 库（com.fasterxml.jackson.core:jackson-databind）</li>
 *   <li>所有方法都是空安全的，null 值不会抛出异常</li>
 *   <li>所有方法都是静态的、无状态的、线程安全的</li>
 *   <li>解析失败时返回 null，不抛出异常</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.core.json;
