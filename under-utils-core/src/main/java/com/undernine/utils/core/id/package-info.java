/**
 * ID 生成包。
 * <p>
 * {@link com.undernine.utils.core.id.IdGenerator} 是主线保留能力，用于本地生成趋势递增的雪花 ID。
 * {@link com.undernine.utils.core.id.UUIDUtils} 仅保留兼容维护；标准 UUID 新代码建议直接使用
 * {@link java.util.UUID}。
 * </p>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>雪花 ID 依赖系统时钟，时钟回拨时会拒绝生成 ID。</li>
 *     <li>多节点部署必须保证 datacenterId 与 workerId 组合唯一。</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.core.id;
