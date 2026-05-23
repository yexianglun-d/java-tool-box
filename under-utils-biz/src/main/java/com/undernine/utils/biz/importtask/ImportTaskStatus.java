package com.undernine.utils.biz.importtask;

/**
 * 导入任务状态。
 *
 * @author Under-Utils Team
 * @version 1.0.1
 * @since 1.0.1
 */
public enum ImportTaskStatus {

    /**
     * 已登记，尚未开始执行。
     */
    PENDING,

    /**
     * 正在执行。
     */
    RUNNING,

    /**
     * 已正常结束。是否存在行级错误需要继续查看 {@link ImportResult}。
     */
    COMPLETED,

    /**
     * 任务级失败，例如读取器异常、配置错误或处理器主动抛出 {@link ImportTaskException}。
     */
    FAILED
}
