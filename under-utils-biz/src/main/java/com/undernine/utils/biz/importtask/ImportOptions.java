package com.undernine.utils.biz.importtask;

/**
 * 导入任务执行选项。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ImportOptions {

    /**
     * maxErrors 为 0 时表示不限制错误数量。
     */
    public static final int UNLIMITED_ERRORS = 0;

    private static final ImportOptions DEFAULT_OPTIONS = builder().build();

    private final boolean failFast;
    private final int maxErrors;
    private final boolean skipBlankRows;
    private final ImportProgressListener progressListener;

    private ImportOptions(Builder builder) {
        this.failFast = builder.failFast;
        this.maxErrors = builder.maxErrors;
        this.skipBlankRows = builder.skipBlankRows;
        this.progressListener = builder.progressListener == null
                ? ImportProgressListener.noop()
                : builder.progressListener;
    }

    /**
     * 返回默认导入选项。
     *
     * @return 默认选项
     */
    public static ImportOptions defaults() {
        return DEFAULT_OPTIONS;
    }

    /**
     * 创建导入选项构建器。
     *
     * @return 构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 基于当前选项创建构建器。
     *
     * @return 构建器
     */
    public Builder toBuilder() {
        return builder()
                .failFast(failFast)
                .maxErrors(maxErrors)
                .skipBlankRows(skipBlankRows)
                .progressListener(progressListener);
    }

    /**
     * 是否在首个失败行后停止。
     *
     * @return true 表示快速失败
     */
    public boolean isFailFast() {
        return failFast;
    }

    /**
     * 最大收集错误数，0 表示不限制。
     *
     * @return 最大错误数
     */
    public int getMaxErrors() {
        return maxErrors;
    }

    /**
     * 是否跳过空白行。
     *
     * @return true 表示跳过空白行
     */
    public boolean isSkipBlankRows() {
        return skipBlankRows;
    }

    /**
     * 获取导入进度监听器。
     *
     * @return 进度监听器
     */
    public ImportProgressListener getProgressListener() {
        return progressListener;
    }

    boolean hasErrorLimit() {
        return maxErrors > UNLIMITED_ERRORS;
    }

    boolean isErrorLimitReached(int errorCount) {
        return hasErrorLimit() && errorCount >= maxErrors;
    }

    /**
     * 导入选项构建器。
     */
    public static final class Builder {

        private boolean failFast;
        private int maxErrors = UNLIMITED_ERRORS;
        private boolean skipBlankRows = true;
        private ImportProgressListener progressListener;

        private Builder() {
        }

        /**
         * 设置是否快速失败。
         *
         * @param failFast true 表示首个失败行后停止
         * @return 当前构建器
         */
        public Builder failFast(boolean failFast) {
            this.failFast = failFast;
            return this;
        }

        /**
         * 设置最大收集错误数，0 表示不限制。
         *
         * @param maxErrors 最大错误数
         * @return 当前构建器
         */
        public Builder maxErrors(int maxErrors) {
            if (maxErrors < UNLIMITED_ERRORS) {
                throw new IllegalArgumentException("maxErrors must be greater than or equal to 0");
            }
            this.maxErrors = maxErrors;
            return this;
        }

        /**
         * 设置是否跳过空白行。
         *
         * @param skipBlankRows true 表示跳过空白行
         * @return 当前构建器
         */
        public Builder skipBlankRows(boolean skipBlankRows) {
            this.skipBlankRows = skipBlankRows;
            return this;
        }

        /**
         * 设置进度监听器。监听器异常会被模板记录并忽略，不影响导入主流程。
         *
         * @param progressListener 进度监听器
         * @return 当前构建器
         */
        public Builder progressListener(ImportProgressListener progressListener) {
            this.progressListener = progressListener;
            return this;
        }

        /**
         * 构建导入选项。
         *
         * @return 导入选项
         */
        public ImportOptions build() {
            return new ImportOptions(this);
        }
    }
}
