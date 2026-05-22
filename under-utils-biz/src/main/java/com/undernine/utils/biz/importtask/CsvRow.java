package com.undernine.utils.biz.importtask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * CSV 导入原始行。
 * <p>
 * 行号表示 CSV 输入源中的 1-based 物理记录起始行号；当 quoted 字段跨多行时，取记录开始行。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class CsvRow {

    private final int rowNumber;
    private final List<String> headers;
    private final List<String> values;
    private final Map<String, Integer> headerIndexes;

    /**
     * 创建 CSV 行。
     *
     * @param rowNumber 1-based 输入源行号
     * @param headers   表头列表，可为空
     * @param values    当前行值列表
     */
    public CsvRow(int rowNumber, List<String> headers, List<String> values) {
        if (rowNumber < 1) {
            throw new IllegalArgumentException("rowNumber must be greater than or equal to 1");
        }
        this.rowNumber = rowNumber;
        this.headers = copy(headers);
        this.values = copy(values);
        this.headerIndexes = buildHeaderIndexes(this.headers);
    }

    /**
     * 输入源中的 1-based 行号。
     *
     * @return 行号
     */
    public int getRowNumber() {
        return rowNumber;
    }

    /**
     * 表头列表。
     *
     * @return 不可变表头列表
     */
    public List<String> getHeaders() {
        return headers;
    }

    /**
     * 当前行值列表。
     *
     * @return 不可变值列表
     */
    public List<String> getValues() {
        return values;
    }

    /**
     * 按 0-based 下标取值。下标越界时返回 null。
     *
     * @param index 值下标
     * @return 单元格值，缺失时为 null
     */
    public String get(int index) {
        if (index < 0 || index >= values.size()) {
            return null;
        }
        return values.get(index);
    }

    /**
     * 按表头取值。未知表头或值缺失时返回 null。
     *
     * @param header 表头名
     * @return 单元格值，缺失时为 null
     */
    public String get(String header) {
        Objects.requireNonNull(header, "header must not be null");
        Integer index = headerIndexes.get(header);
        return index == null ? null : get(index);
    }

    /**
     * 判断是否存在表头。
     *
     * @param header 表头名
     * @return true 表示存在
     */
    public boolean containsHeader(String header) {
        Objects.requireNonNull(header, "header must not be null");
        return headerIndexes.containsKey(header);
    }

    /**
     * 判断当前行是否为空白行。
     *
     * @return true 表示所有值均为空白
     */
    public boolean isBlank() {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return values.toString();
    }

    private static List<String> copy(List<String> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(source));
    }

    private static Map<String, Integer> buildHeaderIndexes(List<String> headers) {
        if (headers.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Integer> indexes = new LinkedHashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            if (header != null) {
                indexes.putIfAbsent(header, i);
            }
        }
        return Collections.unmodifiableMap(indexes);
    }
}
