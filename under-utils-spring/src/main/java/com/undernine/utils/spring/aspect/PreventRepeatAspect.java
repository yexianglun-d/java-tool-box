package com.undernine.utils.spring.aspect;

import com.undernine.utils.spring.annotation.PreventRepeat;
import com.undernine.utils.spring.exception.BizException;
import com.undernine.utils.spring.key.DefaultOperationKeyResolver;
import com.undernine.utils.spring.key.OperationKeyResolver;
import com.undernine.utils.spring.repeat.LocalRepeatSubmitStore;
import com.undernine.utils.spring.repeat.RepeatSubmitStore;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 防重复提交切面。
 * <p>
 * 解析操作 key 后向 {@link RepeatSubmitStore} 登记提交；登记失败时抛出 {@link BizException}。
 * 方法成功执行后 key 保持到 TTL 过期，方法抛异常时按 {@link PreventRepeat#releaseOnFailure()}
 * 决定是否释放 key。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class PreventRepeatAspect implements AutoCloseable {

    private volatile RepeatSubmitStore repeatSubmitStore;
    private volatile boolean defaultStoreOwned;
    private OperationKeyResolver operationKeyResolver = new DefaultOperationKeyResolver();

    @Around("@annotation(preventRepeat)")
    public Object around(ProceedingJoinPoint point, PreventRepeat preventRepeat) throws Throwable {
        String key = operationKeyResolver.resolve(point, preventRepeat.namespace(), preventRepeat.key());
        Duration ttl = Duration.ofMillis(Math.max(1L, preventRepeat.timeUnit().toMillis(preventRepeat.timeout())));
        String message = preventRepeat.message();
        RepeatSubmitStore store = getRepeatSubmitStore();

        if (!store.acquire(key, ttl)) {
            log.warn("【防重复提交】请求被拒绝: {}", key);
            throw new BizException(message);
        }

        try {
            return point.proceed();
        } catch (Throwable e) {
            if (preventRepeat.releaseOnFailure()) {
                store.release(key);
            }
            throw e;
        }
    }

    @Autowired(required = false)
    public synchronized void setRepeatSubmitStore(RepeatSubmitStore repeatSubmitStore) {
        if (repeatSubmitStore != null) {
            closeDefaultStore();
            this.repeatSubmitStore = repeatSubmitStore;
            this.defaultStoreOwned = false;
        }
    }

    @Autowired(required = false)
    public void setOperationKeyResolver(OperationKeyResolver operationKeyResolver) {
        if (operationKeyResolver != null) {
            this.operationKeyResolver = operationKeyResolver;
        }
    }

    @Override
    public synchronized void close() {
        closeDefaultStore();
    }

    private RepeatSubmitStore getRepeatSubmitStore() {
        RepeatSubmitStore store = repeatSubmitStore;
        if (store != null) {
            return store;
        }
        synchronized (this) {
            if (repeatSubmitStore == null) {
                repeatSubmitStore = new LocalRepeatSubmitStore();
                defaultStoreOwned = true;
            }
            return repeatSubmitStore;
        }
    }

    private void closeDefaultStore() {
        if (defaultStoreOwned && repeatSubmitStore instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception ex) {
                log.warn("Failed to close default repeat submit store", ex);
            }
        }
        defaultStoreOwned = false;
    }
}
