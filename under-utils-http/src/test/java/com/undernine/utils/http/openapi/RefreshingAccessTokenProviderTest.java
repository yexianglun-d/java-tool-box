package com.undernine.utils.http.openapi;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshingAccessTokenProviderTest {

    @Test
    void shouldReuseTokenBeforeRefreshWindow() {
        MutableClock clock = new MutableClock(Instant.parse("2026-05-23T00:00:00Z"));
        AtomicInteger fetchCount = new AtomicInteger();
        RefreshingAccessTokenProvider provider = new RefreshingAccessTokenProvider(
                request -> RefreshingAccessTokenProvider.AccessToken.of(
                        "token-" + fetchCount.incrementAndGet(),
                        clock.instant().plusSeconds(60)
                ),
                Duration.ofSeconds(10),
                clock
        );

        String first = provider.getAccessToken(OpenApiRequest.builder().operationName("first").build());
        clock.advance(Duration.ofSeconds(30));
        String second = provider.getAccessToken(OpenApiRequest.builder().operationName("second").build());

        assertThat(first).isEqualTo("token-1");
        assertThat(second).isEqualTo("token-1");
        assertThat(fetchCount).hasValue(1);
    }

    @Test
    void shouldRefreshTokenWhenRefreshWindowReached() {
        MutableClock clock = new MutableClock(Instant.parse("2026-05-23T00:00:00Z"));
        AtomicInteger fetchCount = new AtomicInteger();
        RefreshingAccessTokenProvider provider = new RefreshingAccessTokenProvider(
                request -> RefreshingAccessTokenProvider.AccessToken.of(
                        "token-" + fetchCount.incrementAndGet(),
                        clock.instant().plusSeconds(60)
                ),
                Duration.ofSeconds(10),
                clock
        );

        String first = provider.getAccessToken(OpenApiRequest.builder().build());
        clock.advance(Duration.ofSeconds(55));
        String second = provider.getAccessToken(OpenApiRequest.builder().build());

        assertThat(first).isEqualTo("token-1");
        assertThat(second).isEqualTo("token-2");
        assertThat(fetchCount).hasValue(2);
    }

    @Test
    void shouldInvalidateCachedToken() {
        MutableClock clock = new MutableClock(Instant.parse("2026-05-23T00:00:00Z"));
        AtomicInteger fetchCount = new AtomicInteger();
        RefreshingAccessTokenProvider provider = new RefreshingAccessTokenProvider(
                request -> RefreshingAccessTokenProvider.AccessToken.of(
                        "token-" + fetchCount.incrementAndGet(),
                        clock.instant().plusSeconds(60)
                ),
                Duration.ZERO,
                clock
        );

        assertThat(provider.getAccessToken(OpenApiRequest.builder().build())).isEqualTo("token-1");
        provider.invalidate();
        assertThat(provider.getAccessToken(OpenApiRequest.builder().build())).isEqualTo("token-2");
    }

    @Test
    void shouldRejectExpiredFetchedToken() {
        MutableClock clock = new MutableClock(Instant.parse("2026-05-23T00:00:00Z"));
        RefreshingAccessTokenProvider provider = new RefreshingAccessTokenProvider(
                request -> RefreshingAccessTokenProvider.AccessToken.of("expired", clock.instant()),
                Duration.ZERO,
                clock
        );

        assertThatThrownBy(() -> provider.getAccessToken(OpenApiRequest.builder().build()))
                .isInstanceOf(OpenApiException.class)
                .hasMessage("OpenAPI access token is already expired");
    }

    private static final class MutableClock extends Clock {

        private final AtomicReference<Instant> instant;

        private MutableClock(Instant instant) {
            this.instant = new AtomicReference<>(instant);
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant.get();
        }

        private void advance(Duration duration) {
            instant.updateAndGet(value -> value.plus(duration));
        }
    }
}
