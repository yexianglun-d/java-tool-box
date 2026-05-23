package com.undernine.utils.biz.importtask;

/**
 * 导入进度监听器。
 *
 * @author Under-Utils Team
 * @version 1.0.1
 * @since 1.0.1
 */
@FunctionalInterface
public interface ImportProgressListener {

    /**
     * 接收进度快照。
     *
     * @param progress 当前进度
     */
    void onProgress(ImportProgress progress);

    /**
     * 返回空监听器。
     *
     * @return no-op 监听器
     */
    static ImportProgressListener noop() {
        return progress -> {
        };
    }
}
