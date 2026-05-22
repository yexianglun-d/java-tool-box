package com.undernine.utils.spring.key;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 横切能力 key 解析器。
 * <p>
 * 限流、防重复提交、幂等等能力都需要稳定的业务 key。默认实现支持请求、用户、租户、
 * 方法名和 SpEL 表达式，业务系统可替换为自己的 key 规则。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface OperationKeyResolver {

    /**
     * 解析操作 key。
     *
     * @param point      切点
     * @param namespace  命名空间
     * @param expression key 表达式，可为空
     * @return 操作 key
     */
    String resolve(ProceedingJoinPoint point, String namespace, String expression);
}
