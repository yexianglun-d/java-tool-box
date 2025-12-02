package com.undernine.utils.core.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 日期时间工具类（基于 Java 8+ 的 LocalDateTime）
 * <p>
 * 提供常用的日期时间格式化、解析等方法。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class LocalDateTimeUtils {

    /**
     * 标准日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * 标准日期格式：yyyy-MM-dd
     */
    public static final String PATTERN_DATE = "yyyy-MM-dd";

    /**
     * 标准时间格式：HH:mm:ss
     */
    public static final String PATTERN_TIME = "HH:mm:ss";

    /**
     * 紧凑日期时间格式：yyyyMMddHHmmss
     */
    public static final String PATTERN_DATETIME_COMPACT = "yyyyMMddHHmmss";

    /**
     * 紧凑日期格式：yyyyMMdd
     */
    public static final String PATTERN_DATE_COMPACT = "yyyyMMdd";

    /**
     * 标准日期时间格式化器
     */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATETIME);

    /**
     * 标准日期格式化器
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATE);

    /**
     * 私有构造方法，防止实例化
     *
     * @throws UnsupportedOperationException 如果尝试实例化此类
     */
    private LocalDateTimeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 将 LocalDateTime 格式化为字符串（使用标准格式 yyyy-MM-dd HH:mm:ss）。
     *
     * @param dateTime 待格式化的日期时间
     * @return 格式化后的字符串，如果输入为 null 则返回 null
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? null : DATETIME_FORMATTER.format(dateTime);
    }

    /**
     * 将 LocalDateTime 格式化为字符串（使用指定格式）。
     *
     * @param dateTime 待格式化的日期时间
     * @param pattern  格式模式
     * @return 格式化后的字符串，如果输入为 null 则返回 null
     * @throws IllegalArgumentException 如果 pattern 为空
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be empty");
        }
        return dateTime == null ? null : DateTimeFormatter.ofPattern(pattern).format(dateTime);
    }

    /**
     * 将 LocalDate 格式化为字符串（使用标准格式 yyyy-MM-dd）。
     *
     * @param date 待格式化的日期
     * @return 格式化后的字符串，如果输入为 null 则返回 null
     */
    public static String format(LocalDate date) {
        return date == null ? null : DATE_FORMATTER.format(date);
    }

    /**
     * 解析字符串为 LocalDateTime（使用标准格式 yyyy-MM-dd HH:mm:ss）。
     *
     * @param dateTimeStr 待解析的字符串
     * @return 解析后的 LocalDateTime
     * @throws DateTimeParseException 如果解析失败
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    /**
     * 解析字符串为 LocalDateTime（使用指定格式）。
     *
     * @param dateTimeStr 待解析的字符串
     * @param pattern     格式模式
     * @return 解析后的 LocalDateTime
     * @throws DateTimeParseException   如果解析失败
     * @throws IllegalArgumentException 如果 pattern 为空
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be empty");
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 解析字符串为 LocalDate（使用标准格式 yyyy-MM-dd）。
     *
     * @param dateStr 待解析的字符串
     * @return 解析后的 LocalDate
     * @throws DateTimeParseException 如果解析失败
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * 安全解析字符串为 LocalDateTime，解析失败时返回 null 而不抛异常。
     *
     * @param dateTimeStr 待解析的字符串
     * @return 解析后的 LocalDateTime，解析失败时返回 null
     */
    public static LocalDateTime tryParseDateTime(String dateTimeStr) {
        try {
            return parseDateTime(dateTimeStr);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 安全解析字符串为 LocalDateTime（使用指定格式），解析失败时返回 null 而不抛异常。
     *
     * @param dateTimeStr 待解析的字符串
     * @param pattern     格式模式
     * @return 解析后的 LocalDateTime，解析失败时返回 null
     */
    public static LocalDateTime tryParseDateTime(String dateTimeStr, String pattern) {
        try {
            return parseDateTime(dateTimeStr, pattern);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 获取当前日期时间的字符串表示（使用标准格式 yyyy-MM-dd HH:mm:ss）。
     *
     * @return 当前日期时间字符串
     */
    public static String now() {
        return format(LocalDateTime.now());
    }

    /**
     * 获取当前日期的字符串表示（使用标准格式 yyyy-MM-dd）。
     *
     * @return 当前日期字符串
     */
    public static String today() {
        return format(LocalDate.now());
    }
}
