/**
 * 批量导入任务治理框架。
 * <p>
 * 该包提供纯 Java 编排基座和输入适配层，不绑定数据库、消息队列或具体 Web 框架。
 * 核心模板按 parse、validate、process 三阶段处理每一行，并统一收集行级错误、跳过空白行、
 * fail-fast 和最大错误数等导入治理语义。
 * </p>
 * <p>
 * 业务系统应通过 {@link com.undernine.utils.biz.importtask.ImportRowReader} 适配输入来源，
 * 通过 {@link com.undernine.utils.biz.importtask.ImportRowHandler} 承载行解析、校验和处理逻辑。
 * {@link com.undernine.utils.biz.importtask.ImportTaskException} 表示任务级失败，
 * {@link com.undernine.utils.biz.importtask.RowValidationException} 表示可汇总到当前行的失败。
 * </p>
 */
package com.undernine.utils.biz.importtask;
