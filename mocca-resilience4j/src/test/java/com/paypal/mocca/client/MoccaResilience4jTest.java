package com.paypal.mocca.client;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.function.Function;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MoccaResilience4jTest {
    final FeignDecorators.Builder decoratedBuilder= mock(FeignDecorators.Builder.class);

    @BeforeMethod
    void resetBuilderMock() {
        Mockito.reset(decoratedBuilder);
    }

    @Test
    void delegatedBuild() {
        new MoccaResilience4j.Builder(decoratedBuilder).build();
        verify(decoratedBuilder).build();
    }

    @Test
    void bulkheadTest() {
        final Bulkhead bulkhead = Bulkhead.ofDefaults("defaults");
        new MoccaResilience4j.Builder(decoratedBuilder).bulkhead(bulkhead);
        verify(decoratedBuilder).withBulkhead(bulkhead);
    }

    @Test
    void retryTest() {
        final Retry retry = Retry.ofDefaults("defaults");
        new MoccaResilience4j.Builder(decoratedBuilder).retry(retry);
        verify(decoratedBuilder).withRetry(retry);
    }

    @Test
    void circuitBreakerTest() {
        final CircuitBreaker cb = CircuitBreaker.ofDefaults("defaults");
        new MoccaResilience4j.Builder(decoratedBuilder).circuitBreaker(cb);
        verify(decoratedBuilder).withCircuitBreaker(cb);
    }

    @Test
    void rateLimiterTest() {
        final RateLimiter rl = RateLimiter.ofDefaults("defaults");
        new MoccaResilience4j.Builder(decoratedBuilder).rateLimiter(rl);
        verify(decoratedBuilder).withRateLimiter(rl);
    }

    @Test
    void fallBackTest() {
        final Object fallback = Mockito.any();
        new MoccaResilience4j.Builder(decoratedBuilder).fallback(fallback);
        verify(decoratedBuilder).withFallback(fallback);
    }

    @Test
    void fallBackFactoryTest() {
        final Function<Exception, ?> fallbackFactory = Mockito.any();
        new MoccaResilience4j.Builder(decoratedBuilder).fallbackFactory(fallbackFactory);
        verify(decoratedBuilder).withFallbackFactory(fallbackFactory);
    }
}
