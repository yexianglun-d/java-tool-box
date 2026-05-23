package com.undernine.utils.http.openapi;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * 带本地缓存和提前刷新窗口的 access token 提供者。
 * <p>
 * 适用于开放平台 token 有有效期、多个业务调用共享 token 的场景。该实现只负责单 JVM 内的缓存和并发收敛，
 * 多实例共享 token、持久化或分布式锁应由业务项目在 {@link TokenFetcher} 内处理。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.1
 * @since 1.0.1
 */
public final class RefreshingAccessTokenProvider implements AccessTokenProvider {

    private final TokenFetcher tokenFetcher;
    private final Duration refreshAhead;
    private final Clock clock;
    private volatile AccessToken cachedToken;

    /**
     * 创建 token 提供者。
     *
     * @param tokenFetcher token 拉取器
     * @param refreshAhead 到期前多久主动刷新，null 表示不提前刷新
     */
    public RefreshingAccessTokenProvider(TokenFetcher tokenFetcher, Duration refreshAhead) {
        this(tokenFetcher, refreshAhead, Clock.systemUTC());
    }

    /**
     * 创建 token 提供者。
     *
     * @param tokenFetcher token 拉取器
     * @param refreshAhead 到期前多久主动刷新，null 表示不提前刷新
     * @param clock        时钟
     */
    public RefreshingAccessTokenProvider(TokenFetcher tokenFetcher, Duration refreshAhead, Clock clock) {
        this.tokenFetcher = Objects.requireNonNull(tokenFetcher, "tokenFetcher must not be null");
        this.refreshAhead = normalizeRefreshAhead(refreshAhead);
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    public String getAccessToken(OpenApiRequest request) {
        AccessToken token = cachedToken;
        if (isUsable(token)) {
            return token.getValue();
        }
        synchronized (this) {
            token = cachedToken;
            if (isUsable(token)) {
                return token.getValue();
            }
            AccessToken fetchedToken = tokenFetcher.fetch(request);
            validateToken(fetchedToken);
            cachedToken = fetchedToken;
            return fetchedToken.getValue();
        }
    }

    /**
     * 清除当前缓存 token，下次请求会重新拉取。
     */
    public synchronized void invalidate() {
        cachedToken = null;
    }

    private boolean isUsable(AccessToken token) {
        if (token == null || isBlank(token.getValue())) {
            return false;
        }
        Instant expiresAt = token.getExpiresAt();
        if (expiresAt == null) {
            return true;
        }
        Instant refreshAt = expiresAt.minus(refreshAhead);
        return Instant.now(clock).isBefore(refreshAt);
    }

    private void validateToken(AccessToken token) {
        if (token == null || isBlank(token.getValue())) {
            throw new OpenApiException("OpenAPI access token cannot be blank");
        }
        Instant expiresAt = token.getExpiresAt();
        if (expiresAt != null && !Instant.now(clock).isBefore(expiresAt)) {
            throw new OpenApiException("OpenAPI access token is already expired");
        }
    }

    private Duration normalizeRefreshAhead(Duration refreshAhead) {
        if (refreshAhead == null || refreshAhead.isNegative()) {
            return Duration.ZERO;
        }
        return refreshAhead;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * token 拉取器。
     */
    @FunctionalInterface
    public interface TokenFetcher {

        /**
         * 拉取 access token。
         *
         * @param request 当前开放平台请求
         * @return access token
         */
        AccessToken fetch(OpenApiRequest request);
    }

    /**
     * access token 值和过期时间。
     */
    public static final class AccessToken {

        private final String value;
        private final Instant expiresAt;

        /**
         * 构造 access token。
         *
         * @param value     token 值
         * @param expiresAt 过期时间，null 表示不过期
         */
        public AccessToken(String value, Instant expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }

        /**
         * 创建带过期时间的 token。
         *
         * @param value     token 值
         * @param expiresAt 过期时间
         * @return access token
         */
        public static AccessToken of(String value, Instant expiresAt) {
            return new AccessToken(value, expiresAt);
        }

        /**
         * 创建不过期 token。
         *
         * @param value token 值
         * @return access token
         */
        public static AccessToken nonExpiring(String value) {
            return new AccessToken(value, null);
        }

        public String getValue() {
            return value;
        }

        public Instant getExpiresAt() {
            return expiresAt;
        }
    }
}
