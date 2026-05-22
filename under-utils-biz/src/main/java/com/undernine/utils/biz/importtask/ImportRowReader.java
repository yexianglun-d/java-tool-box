package com.undernine.utils.biz.importtask;

/**
 * 导入行读取器。
 * <p>
 * 读取器负责把 CSV、Excel、文本等输入源适配为统一的行迭代模型，并在导入结束后释放底层资源。
 * </p>
 *
 * @param <R> 原始行类型
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ImportRowReader<R> extends Iterable<R>, AutoCloseable {

    /**
     * 关闭底层输入资源。
     *
     * @throws Exception 关闭失败
     */
    @Override
    void close() throws Exception;
}
