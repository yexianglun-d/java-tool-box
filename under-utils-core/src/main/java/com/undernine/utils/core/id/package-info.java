/**
 * ID 生成工具包
 * <p>
 * 提供多种 ID 生成工具类，包括雪花算法、UUID 等。
 * </p>
 *
 * <h2>主要工具类</h2>
 * <ul>
 *   <li>{@link com.undernine.utils.core.id.IdGenerator} - 雪花算法 ID 生成器</li>
 *   <li>{@link com.undernine.utils.core.id.UUIDUtils} - UUID 生成工具类</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>雪花算法 ID</h3>
 * <pre>{@code
 * // 生成 Long 类型 ID
 * long id = IdGenerator.nextId();
 *
 * // 生成 String 类型 ID
 * String idStr = IdGenerator.nextIdStr();
 *
 * // 解析 ID 信息
 * IdGenerator.IdInfo info = IdGenerator.parseId(id);
 * System.out.println("时间戳: " + info.getTimestamp());
 * System.out.println("数据中心ID: " + info.getDatacenterId());
 * System.out.println("机器ID: " + info.getWorkerId());
 * System.out.println("序列号: " + info.getSequence());
 * }</pre>
 *
 * <h3>UUID</h3>
 * <pre>{@code
 * // 生成标准 UUID
 * String uuid = UUIDUtils.randomUUID();  // 550e8400-e29b-41d4-a716-446655440000
 *
 * // 生成不带连字符的 UUID
 * String uuidNoDash = UUIDUtils.randomUUIDNoDash();  // 550e8400e29b41d4a716446655440000
 *
 * // 生成短 UUID（22 位）
 * String shortUuid = UUIDUtils.shortUUID();
 *
 * // 基于名称生成 UUID（确定性）
 * String nameUuid = UUIDUtils.nameUUIDFromString("test");
 *
 * // 生成有序 UUID（适合数据库索引）
 * String timeUuid = UUIDUtils.timeBasedUUID();
 *
 * // 验证 UUID 格式
 * boolean isValid = UUIDUtils.isValidUUID(uuid);
 * }</pre>
 *
 * <h2>特性对比</h2>
 * <table border="1">
 *   <tr>
 *     <th>特性</th>
 *     <th>雪花算法</th>
 *     <th>UUID</th>
 *   </tr>
 *   <tr>
 *     <td>长度</td>
 *     <td>19 位数字（Long）</td>
 *     <td>36 位字符串（含连字符）</td>
 *   </tr>
 *   <tr>
 *     <td>有序性</td>
 *     <td>有序（时间递增）</td>
 *     <td>无序（随机 UUID）</td>
 *   </tr>
 *   <tr>
 *     <td>性能</td>
 *     <td>极高（内存生成）</td>
 *     <td>高（内存生成）</td>
 *   </tr>
 *   <tr>
 *     <td>唯一性</td>
 *     <td>分布式唯一</td>
 *     <td>全局唯一</td>
 *   </tr>
 *   <tr>
 *     <td>适用场景</td>
 *     <td>数据库主键、订单号</td>
 *     <td>文件名、临时 ID</td>
 *   </tr>
 * </table>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>雪花算法需要配置数据中心 ID 和机器 ID，确保分布式环境下的唯一性</li>
 *   <li>雪花算法依赖系统时钟，时钟回拨会导致 ID 重复</li>
 *   <li>UUID 是全局唯一的，但无序，不适合作为数据库主键（索引性能差）</li>
 *   <li>所有方法都是静态的、无状态的、线程安全的</li>
 *   <li>仅依赖 JDK，不依赖任何第三方框架</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.core.id;
