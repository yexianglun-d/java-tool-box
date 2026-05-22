/**
 * Under-Utils 低耦合基础模块。
 * <p>
 * 本包承载少量无 Spring 依赖的基础能力和历史工具兼容入口。Under-Utils 不以复制 Hutool、
 * Apache Commons、Guava 或 JDK 工具方法为目标；新增 API 必须具备清晰工程边界和复用价值。
 * </p>
 *
 * <h2>主线保留能力</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.core.id.IdGenerator} - 雪花算法 ID 生成器。</li>
 *     <li>{@link com.undernine.utils.core.money.MoneyUtils} - BigDecimal 金额计算和分/元转换。</li>
 * </ul>
 *
 * <h2>兼容维护能力</h2>
 * <p>
 * 字符串、集合、日期、正则校验、UUID、摘要、AES 和 JSON 等历史工具入口会保留兼容，
 * 但不再作为新增工具方法的扩张方向。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.core;
