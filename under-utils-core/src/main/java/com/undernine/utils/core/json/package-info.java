/**
 * JSON 历史工具兼容包。
 * <p>
 * {@link com.undernine.utils.core.json.JsonUtils} 使用模块内置单例 Jackson
 * {@link com.fasterxml.jackson.databind.ObjectMapper}。该入口仅保留兼容维护；
 * Spring Boot 或复杂应用应优先注入应用自己的 ObjectMapper、消息 codec 或边界更明确的序列化组件。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.core.json;
