package com.undernine.utils.spring.context;

import org.springframework.core.task.TaskDecorator;

/**
 * 传播 {@link OperationContext} 的 Spring {@link TaskDecorator}。
 * <p>
 * starter 或业务应用可将该类配置到 Spring 线程池，使异步任务在执行线程中获得提交线程的
 * {@link OperationContext}，并在执行完成后恢复执行线程原有上下文。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class OperationContextTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        return OperationContextSnapshot.capture().wrap(runnable);
    }
}
