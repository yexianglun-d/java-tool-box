package com.undernine.utils.ai;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 流式文本对话响应。
 * <p>
 * 该对象只能消费一次，且必须关闭以释放底层 HTTP 连接。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public final class ChatStream implements Iterable<ChatStreamEvent>, AutoCloseable {

    private final Iterator<ChatStreamEvent> iterator;
    private final AutoCloseable closeAction;
    private final AtomicBoolean iteratorOpened = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();

    /**
     * 创建流式响应。
     *
     * @param iterator 事件迭代器
     * @param closeAction 关闭动作
     */
    public ChatStream(Iterator<ChatStreamEvent> iterator, AutoCloseable closeAction) {
        this.iterator = iterator;
        this.closeAction = closeAction;
    }

    @Override
    public Iterator<ChatStreamEvent> iterator() {
        if (!iteratorOpened.compareAndSet(false, true)) {
            throw new IllegalStateException("chat stream can only be consumed once");
        }
        return iterator;
    }

    /**
     * 转为 Java Stream。
     *
     * @return Java Stream
     */
    public Stream<ChatStreamEvent> stream() {
        Spliterator<ChatStreamEvent> spliterator = Spliterators.spliteratorUnknownSize(iterator(),
                Spliterator.ORDERED | Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, false).onClose(this::close);
    }

    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        if (closeAction == null) {
            return;
        }
        try {
            closeAction.close();
        } catch (Exception e) {
            throw new AiException(AiErrorType.NETWORK, "Failed to close AI stream", 0, null, false, e);
        }
    }
}
