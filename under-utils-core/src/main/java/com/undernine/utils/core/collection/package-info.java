/**
 * 集合工具包
 * <p>
 * 提供常用的集合处理工具类，包括集合判空、转换、过滤、分组等功能。
 * </p>
 *
 * <h2>主要工具类</h2>
 * <ul>
 *   <li>{@link com.undernine.utils.core.collection.CollectionUtils} - 集合工具类</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>集合判空</h3>
 * <pre>{@code
 * List<String> list = Arrays.asList("a", "b", "c");
 * boolean isEmpty = CollectionUtils.isEmpty(list);  // false
 * boolean isNotEmpty = CollectionUtils.isNotEmpty(list);  // true
 * }</pre>
 *
 * <h3>集合转换</h3>
 * <pre>{@code
 * // List 转 Set
 * List<String> list = Arrays.asList("a", "b", "c", "a");
 * Set<String> set = CollectionUtils.toSet(list);  // [a, b, c]
 *
 * // 数组转 List
 * String[] array = {"a", "b", "c"};
 * List<String> list = CollectionUtils.toList(array);
 * }</pre>
 *
 * <h3>集合过滤</h3>
 * <pre>{@code
 * List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
 * List<Integer> evenNumbers = CollectionUtils.filter(numbers, n -> n % 2 == 0);  // [2, 4]
 * }</pre>
 *
 * <h3>集合映射</h3>
 * <pre>{@code
 * List<String> names = Arrays.asList("alice", "bob", "charlie");
 * List<String> upperNames = CollectionUtils.map(names, String::toUpperCase);  // [ALICE, BOB, CHARLIE]
 * }</pre>
 *
 * <h3>集合分组</h3>
 * <pre>{@code
 * List<String> words = Arrays.asList("apple", "banana", "apricot", "blueberry");
 * Map<Character, List<String>> grouped = CollectionUtils.groupBy(words, w -> w.charAt(0));
 * // {a=[apple, apricot], b=[banana, blueberry]}
 * }</pre>
 *
 * <h3>集合分区</h3>
 * <pre>{@code
 * List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
 * List<List<Integer>> partitions = CollectionUtils.partition(numbers, 3);
 * // [[1, 2, 3], [4, 5, 6], [7, 8, 9], [10]]
 * }</pre>
 *
 * <h3>集合去重</h3>
 * <pre>{@code
 * List<String> list = Arrays.asList("a", "b", "c", "a", "b");
 * List<String> distinct = CollectionUtils.distinct(list);  // [a, b, c]
 * }</pre>
 *
 * <h3>集合交集、并集、差集</h3>
 * <pre>{@code
 * List<Integer> list1 = Arrays.asList(1, 2, 3, 4);
 * List<Integer> list2 = Arrays.asList(3, 4, 5, 6);
 *
 * List<Integer> intersection = CollectionUtils.intersection(list1, list2);  // [3, 4]
 * List<Integer> union = CollectionUtils.union(list1, list2);  // [1, 2, 3, 4, 5, 6]
 * List<Integer> difference = CollectionUtils.difference(list1, list2);  // [1, 2]
 * }</pre>
 *
 * <h2>特性</h2>
 * <ul>
 *   <li>支持 List、Set、Map 等常用集合类型</li>
 *   <li>支持 Lambda 表达式和方法引用</li>
 *   <li>提供丰富的集合操作方法</li>
 *   <li>所有方法都是空安全的</li>
 *   <li>所有方法都是静态的、无状态的、线程安全的</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>所有方法都是空安全的，null 值不会抛出异常</li>
 *   <li>所有方法都是静态的、无状态的、线程安全的</li>
 *   <li>仅依赖 JDK，不依赖任何第三方框架</li>
 *   <li>返回的集合都是新集合，不会修改原集合</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.core.collection;
