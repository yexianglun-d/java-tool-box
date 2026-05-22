package com.undernine.utils.biz.importtask;

import java.util.Collections;
import java.util.List;

/**
 * 导入行处理器。
 *
 * @param <R> 原始行类型
 * @param <T> 解析后的业务行类型
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ImportRowHandler<R, T> {

    /**
     * 判断原始行是否为空白行。默认仅将 null 视为空白行。
     *
     * @param rawRow 原始行
     * @return true 表示空白行
     */
    default boolean isBlankRow(R rawRow) {
        return rawRow == null;
    }

    /**
     * 解析原始行。
     *
     * @param rawRow  原始行
     * @param context 当前行上下文
     * @return 解析后的业务行
     * @throws Exception 解析异常
     */
    T parse(R rawRow, ImportRowContext context) throws Exception;

    /**
     * 校验业务行，可返回多个行级错误。
     *
     * @param row     解析后的业务行
     * @param context 当前行上下文
     * @return 行级错误列表，空列表表示校验通过
     * @throws Exception 校验异常
     */
    default List<ImportRowError> validate(T row, ImportRowContext context) throws Exception {
        return Collections.emptyList();
    }

    /**
     * 处理校验通过的业务行。
     *
     * @param row     解析后的业务行
     * @param context 当前行上下文
     * @throws Exception 处理异常
     */
    void process(T row, ImportRowContext context) throws Exception;

    /**
     * 生成原始行摘要，用于错误收集。默认使用 String.valueOf(rawRow)。
     *
     * @param rawRow 原始行
     * @return 原始行摘要
     */
    default String summarizeRawRow(R rawRow) {
        return String.valueOf(rawRow);
    }
}
