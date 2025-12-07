package com.undernine.utils.core.id;

/**
 * 分布式 ID 生成器（雪花算法 Snowflake）
 * <p>
 * 基于 Twitter 的 Snowflake 算法实现的分布式唯一 ID 生成器。
 * </p>
 * <p>
 * ID 结构（64 位）：
 * <ul>
 *   <li>1 位：符号位（始终为 0）</li>
 *   <li>41 位：时间戳（毫秒级，可使用约 69 年）</li>
 *   <li>10 位：工作机器 ID（5 位数据中心 ID + 5 位机器 ID，支持 1024 个节点）</li>
 *   <li>12 位：序列号（同一毫秒内最多生成 4096 个 ID）</li>
 * </ul>
 * </p>
 * <p>
 * 特性：
 * <ul>
 *   <li>全局唯一</li>
 *   <li>趋势递增</li>
 *   <li>高性能（单机每秒可生成百万级 ID）</li>
 *   <li>时间有序</li>
 *   <li>无需依赖数据库或第三方服务</li>
 * </ul>
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class IdGenerator {

    /**
     * 起始时间戳（2024-01-01 00:00:00）
     */
    private static final long START_TIMESTAMP = 1704038400000L;

    /**
     * 数据中心 ID 占用的位数
     */
    private static final long DATACENTER_ID_BITS = 5L;

    /**
     * 机器 ID 占用的位数
     */
    private static final long WORKER_ID_BITS = 5L;

    /**
     * 序列号占用的位数
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 数据中心 ID 的最大值（31）
     */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    /**
     * 机器 ID 的最大值（31）
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * 序列号的最大值（4095）
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * 机器 ID 左移位数（12）
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据中心 ID 左移位数（17）
     */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 时间戳左移位数（22）
     */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    /**
     * 数据中心 ID
     */
    private final long datacenterId;

    /**
     * 机器 ID
     */
    private final long workerId;

    /**
     * 序列号
     */
    private long sequence = 0L;

    /**
     * 上次生成 ID 的时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 构造方法（数据中心 ID 和机器 ID 都为 0）。
     */
    public IdGenerator() {
        this(0, 0);
    }

    /**
     * 构造方法。
     *
     * @param datacenterId 数据中心 ID（0-31）
     * @param workerId     机器 ID（0-31）
     * @throws IllegalArgumentException 如果数据中心 ID 或机器 ID 超出范围
     */
    public IdGenerator(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("Datacenter ID must be between 0 and %d", MAX_DATACENTER_ID));
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("Worker ID must be between 0 and %d", MAX_WORKER_ID));
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * 生成下一个 ID（线程安全）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * IdGenerator generator = new IdGenerator(1, 1);
     * long id = generator.nextId();
     * }</pre>
     * </p>
     *
     * @return 唯一 ID
     */
    public synchronized long nextId() {
        long timestamp = getCurrentTimestamp();

        // 时钟回拨检测
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }

        // 同一毫秒内
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 序列号溢出，等待下一毫秒
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒，序列号重置为 0
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 组装 ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 生成下一个 ID（字符串形式）。
     *
     * @return ID 字符串
     */
    public String nextIdStr() {
        return String.valueOf(nextId());
    }

    /**
     * 解析 ID，获取其包含的信息。
     * <p>
     * 使用示例：
     * <pre>{@code
     * IdGenerator generator = new IdGenerator(1, 1);
     * long id = generator.nextId();
     * IdInfo info = generator.parseId(id);
     * System.out.println("时间戳: " + info.getTimestamp());
     * }</pre>
     * </p>
     *
     * @param id ID 值
     * @return ID 信息对象
     */
    public IdInfo parseId(long id) {
        long timestamp = (id >> TIMESTAMP_SHIFT) + START_TIMESTAMP;
        long datacenter = (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
        long worker = (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
        long seq = id & MAX_SEQUENCE;

        return new IdInfo(id, timestamp, datacenter, worker, seq);
    }

    /**
     * 获取当前时间戳（毫秒）
     *
     * @return 当前时间戳
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 等待下一毫秒
     *
     * @param lastTimestamp 上次时间戳
     * @return 下一毫秒的时间戳
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    /**
     * ID 信息类
     */
    public static class IdInfo {
        /**
         * 原始 ID
         */
        private final long id;

        /**
         * 时间戳
         */
        private final long timestamp;

        /**
         * 数据中心 ID
         */
        private final long datacenterId;

        /**
         * 机器 ID
         */
        private final long workerId;

        /**
         * 序列号
         */
        private final long sequence;

        public IdInfo(long id, long timestamp, long datacenterId, long workerId, long sequence) {
            this.id = id;
            this.timestamp = timestamp;
            this.datacenterId = datacenterId;
            this.workerId = workerId;
            this.sequence = sequence;
        }

        public long getId() {
            return id;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public long getDatacenterId() {
            return datacenterId;
        }

        public long getWorkerId() {
            return workerId;
        }

        public long getSequence() {
            return sequence;
        }

        @Override
        public String toString() {
            return "IdInfo{" +
                    "id=" + id +
                    ", timestamp=" + timestamp +
                    ", datacenterId=" + datacenterId +
                    ", workerId=" + workerId +
                    ", sequence=" + sequence +
                    '}';
        }
    }
}
