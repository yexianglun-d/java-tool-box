package com.undernine.utils.core.collection;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 集合工具类。
 * <p>
 * 该类仅保留为兼容维护 API。新代码应优先使用 JDK Collections、Stream API 或业务内专用转换流程，
 * Under-Utils 不再扩展低复杂度集合工具方法。
 * </p>
 * <p>
 * 特性：
 * <ul>
 *   <li>所有方法都是空安全的，避免 NullPointerException</li>
 *   <li>提供 Collection 和 Map 的判空方法</li>
 *   <li>提供安全的集合元素访问方法</li>
 *   <li>提供集合分批、去重、转换等常用操作</li>
 * </ul>
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 * @deprecated 历史基础工具保留为兼容 API，不作为 Under-Utils 后续工程模式主线能力演进。
 */
@Deprecated(since = "1.0.0")
public final class CollectionUtils {

    /**
     * 私有构造方法，防止实例化
     *
     * @throws UnsupportedOperationException 如果尝试实例化此类
     */
    private CollectionUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== Collection 判空 ====================

    /**
     * 判断集合是否为空（null 或不含任何元素）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<String> list = null;
     * boolean empty = CollectionUtils.isEmpty(list); // true
     * }</pre>
     * </p>
     *
     * @param collection 待判断的集合
     * @return 当集合为 null 或为空时返回 true，否则返回 false
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否非空（不为 null 且包含至少一个元素）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<String> list = Arrays.asList("a", "b");
     * boolean notEmpty = CollectionUtils.isNotEmpty(list); // true
     * }</pre>
     * </p>
     *
     * @param collection 待判断的集合
     * @return 当集合不为 null 且不为空时返回 true，否则返回 false
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    // ==================== Map 判空 ====================

    /**
     * 判断 Map 是否为空（null 或不含任何键值对）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * Map<String, Object> map = null;
     * boolean empty = CollectionUtils.isEmpty(map); // true
     * }</pre>
     * </p>
     *
     * @param map 待判断的 Map
     * @return 当 Map 为 null 或为空时返回 true，否则返回 false
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断 Map 是否非空（不为 null 且包含至少一个键值对）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * Map<String, Object> map = new HashMap<>();
     * map.put("key", "value");
     * boolean notEmpty = CollectionUtils.isNotEmpty(map); // true
     * }</pre>
     * </p>
     *
     * @param map 待判断的 Map
     * @return 当 Map 不为 null 且不为空时返回 true，否则返回 false
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    // ==================== 集合大小 ====================

    /**
     * 获取集合的大小（元素个数）。
     * <p>
     * 如果集合为 null，返回 0。
     * </p>
     *
     * @param collection 待获取大小的集合
     * @return 集合的大小，如果为 null 则返回 0
     */
    public static int size(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    /**
     * 获取 Map 的大小（键值对个数）。
     * <p>
     * 如果 Map 为 null，返回 0。
     * </p>
     *
     * @param map 待获取大小的 Map
     * @return Map 的大小，如果为 null 则返回 0
     */
    public static int size(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }

    // ==================== 安全访问元素 ====================

    /**
     * 安全地获取 List 的第一个元素。
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<String> list = Arrays.asList("a", "b", "c");
     * String first = CollectionUtils.getFirst(list); // "a"
     * }</pre>
     * </p>
     *
     * @param list 待获取元素的 List
     * @param <T>  元素类型
     * @return 第一个元素，如果 List 为 null 或为空则返回 null
     */
    public static <T> T getFirst(List<T> list) {
        return isEmpty(list) ? null : list.get(0);
    }

    /**
     * 安全地获取 List 的最后一个元素。
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<String> list = Arrays.asList("a", "b", "c");
     * String last = CollectionUtils.getLast(list); // "c"
     * }</pre>
     * </p>
     *
     * @param list 待获取元素的 List
     * @param <T>  元素类型
     * @return 最后一个元素，如果 List 为 null 或为空则返回 null
     */
    public static <T> T getLast(List<T> list) {
        return isEmpty(list) ? null : list.get(list.size() - 1);
    }

    /**
     * 安全地获取 List 指定索引位置的元素。
     * <p>
     * 如果索引越界或 List 为 null，返回 null 而不抛出异常。
     * </p>
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<String> list = Arrays.asList("a", "b", "c");
     * String element = CollectionUtils.get(list, 1); // "b"
     * String outOfBounds = CollectionUtils.get(list, 10); // null
     * }</pre>
     * </p>
     *
     * @param list  待获取元素的 List
     * @param index 索引位置
     * @param <T>   元素类型
     * @return 指定位置的元素，如果越界或 List 为 null 则返回 null
     */
    public static <T> T get(List<T> list, int index) {
        if (isEmpty(list) || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    /**
     * 安全地获取 List 指定索引位置的元素，如果越界则返回默认值。
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<String> list = Arrays.asList("a", "b", "c");
     * String element = CollectionUtils.get(list, 10, "default"); // "default"
     * }</pre>
     * </p>
     *
     * @param list         待获取元素的 List
     * @param index        索引位置
     * @param defaultValue 默认值
     * @param <T>          元素类型
     * @return 指定位置的元素，如果越界或 List 为 null 则返回默认值
     */
    public static <T> T get(List<T> list, int index, T defaultValue) {
        T value = get(list, index);
        return value != null ? value : defaultValue;
    }

    // ==================== 集合操作 ====================

    /**
     * 将 List 分批处理（分页）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
     * List<List<Integer>> batches = CollectionUtils.partition(list, 3);
     * // 结果：[[1, 2, 3], [4, 5, 6], [7]]
     * }</pre>
     * </p>
     *
     * @param list      待分批的 List
     * @param batchSize 每批的大小
     * @param <T>       元素类型
     * @return 分批后的 List，如果原 List 为 null 或空则返回空 List
     * @throws IllegalArgumentException 如果 batchSize 小于等于 0
     */
    public static <T> List<List<T>> partition(List<T> list, int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be greater than 0");
        }
        if (isEmpty(list)) {
            return new ArrayList<>();
        }

        List<List<T>> result = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i += batchSize) {
            int end = Math.min(i + batchSize, size);
            result.add(new ArrayList<>(list.subList(i, end)));
        }
        return result;
    }

    /**
     * 对集合去重（保持原有顺序）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<String> list = Arrays.asList("a", "b", "a", "c", "b");
     * List<String> distinct = CollectionUtils.distinct(list);
     * // 结果：["a", "b", "c"]
     * }</pre>
     * </p>
     *
     * @param collection 待去重的集合
     * @param <T>        元素类型
     * @return 去重后的 List，如果原集合为 null 或空则返回空 List
     */
    public static <T> List<T> distinct(Collection<T> collection) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(new LinkedHashSet<>(collection));
    }

    /**
     * 集合转换（映射）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
     * List<Integer> lengths = CollectionUtils.map(names, String::length);
     * // 结果：[5, 3, 7]
     * }</pre>
     * </p>
     *
     * @param collection 待转换的集合
     * @param mapper     转换函数
     * @param <T>        原始元素类型
     * @param <R>        目标元素类型
     * @return 转换后的 List，如果原集合为 null 或空则返回空 List
     */
    public static <T, R> List<R> map(Collection<T> collection, Function<T, R> mapper) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return collection.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * 将数组转换为 List。
     * <p>
     * 注意：返回的是可变 List，与 Arrays.asList() 不同。
     * </p>
     * <p>
     * 使用示例：
     * <pre>{@code
     * String[] array = {"a", "b", "c"};
     * List<String> list = CollectionUtils.toList(array);
     * }</pre>
     * </p>
     *
     * @param array 待转换的数组
     * @param <T>   元素类型
     * @return 转换后的可变 List，如果数组为 null 或空则返回空 List
     */
    @SafeVarargs
    public static <T> List<T> toList(T... array) {
        if (array == null || array.length == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(array));
    }

    /**
     * 批量添加元素到集合。
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<String> list = new ArrayList<>();
     * CollectionUtils.addAll(list, "a", "b", "c");
     * }</pre>
     * </p>
     *
     * @param collection 目标集合
     * @param elements   待添加的元素
     * @param <T>        元素类型
     * @return 是否添加成功
     */
    @SafeVarargs
    public static <T> boolean addAll(Collection<T> collection, T... elements) {
        if (collection == null || elements == null || elements.length == 0) {
            return false;
        }
        return Collections.addAll(collection, elements);
    }

    // ==================== 集合运算 ====================

    /**
     * 集合并集（合并两个集合并去重）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<String> list1 = Arrays.asList("a", "b", "c");
     * List<String> list2 = Arrays.asList("b", "c", "d");
     * List<String> union = CollectionUtils.union(list1, list2);
     * // 结果：["a", "b", "c", "d"]
     * }</pre>
     * </p>
     *
     * @param coll1 第一个集合
     * @param coll2 第二个集合
     * @param <T>   元素类型
     * @return 并集结果，如果两个集合都为 null 或空则返回空 List
     */
    public static <T> List<T> union(Collection<T> coll1, Collection<T> coll2) {
        Set<T> set = new LinkedHashSet<>();
        if (isNotEmpty(coll1)) {
            set.addAll(coll1);
        }
        if (isNotEmpty(coll2)) {
            set.addAll(coll2);
        }
        return new ArrayList<>(set);
    }

    /**
     * 集合交集（获取两个集合的公共元素）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<String> list1 = Arrays.asList("a", "b", "c");
     * List<String> list2 = Arrays.asList("b", "c", "d");
     * List<String> intersection = CollectionUtils.intersection(list1, list2);
     * // 结果：["b", "c"]
     * }</pre>
     * </p>
     *
     * @param coll1 第一个集合
     * @param coll2 第二个集合
     * @param <T>   元素类型
     * @return 交集结果，如果任一集合为 null 或空则返回空 List
     */
    public static <T> List<T> intersection(Collection<T> coll1, Collection<T> coll2) {
        if (isEmpty(coll1) || isEmpty(coll2)) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>();
        Set<T> set2 = new HashSet<>(coll2);
        for (T element : coll1) {
            if (set2.contains(element)) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * 集合差集（获取第一个集合中不在第二个集合中的元素）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<String> list1 = Arrays.asList("a", "b", "c");
     * List<String> list2 = Arrays.asList("b", "c", "d");
     * List<String> subtract = CollectionUtils.subtract(list1, list2);
     * // 结果：["a"]
     * }</pre>
     * </p>
     *
     * @param coll1 第一个集合
     * @param coll2 第二个集合
     * @param <T>   元素类型
     * @return 差集结果，如果第一个集合为 null 或空则返回空 List
     */
    public static <T> List<T> subtract(Collection<T> coll1, Collection<T> coll2) {
        if (isEmpty(coll1)) {
            return new ArrayList<>();
        }
        if (isEmpty(coll2)) {
            return new ArrayList<>(coll1);
        }
        List<T> result = new ArrayList<>();
        Set<T> set2 = new HashSet<>(coll2);
        for (T element : coll1) {
            if (!set2.contains(element)) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * 判断集合是否包含指定元素（空安全）。
     *
     * @param collection 集合
     * @param element    待查找的元素
     * @param <T>        元素类型
     * @return 如果集合包含该元素返回 true，否则返回 false
     */
    public static <T> boolean contains(Collection<T> collection, T element) {
        return isNotEmpty(collection) && collection.contains(element);
    }
}
