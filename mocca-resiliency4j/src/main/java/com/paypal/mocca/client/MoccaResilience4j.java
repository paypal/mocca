package com.paypal.mocca.client;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Mocca supports Resilience4j-based resilience.
 * <br>
 * The example below shows how to configure resilience features in a Mocca client using Resilience4j:
 * <pre><code>
 * import io.github.resilience4j.circuitbreaker.CircuitBreaker;
 * import com.paypal.mocca.client.MoccaResilience4j;
 *
 * ...
 *
 * CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("BooksAppClient-cb");
 *
 * MoccaResilience4j moccaResilience = new MoccaResilience4j.Builder()
 *     .circuitBreaker(circuitBreaker)
 *     .build();
 *
 * BooksAppClient client = MoccaClient.Builder
 *     .sync("localhost:8080/booksapp")
 *     .resiliency(moccaResilience)
 *     .build(BooksAppClient.class);
 * </code></pre>
 * Notice that {@link Builder} is used to create a {@link MoccaResilience4j} object containing the application specific Resilience4j configuration.
 * <br>
 * A few important notes:
 * <ol>
 *     <li>Mocca Resilience4j feature is only supported by clients created with {@link com.paypal.mocca.client.MoccaClient.Builder.SyncBuilder#sync(String)} (as seen in the example above). Clients created with {@link com.paypal.mocca.client.MoccaClient.Builder.AsyncBuilder#async(String)} don't support Resilience4j at the moment.</li>
 *     <li>Although the example above only shows the setting of a circuit breaker, the following additional Resilience4j features are supported.
 *         <ol>
 *             <li>Retry</li>
 *             <li>Rate limiting</li>
 *             <li>Bulkhead</li>
 *             <li>Fallback</li>
 *         </ol>
 *     </li>
 *     <li>The order of registering each resilience feature in {@link MoccaResilience4j} matters. More details at the next subsection.</li>
 * </ol>
 * <br>
 * <p><strong>Resilience features execution order</strong></p>
 * It is very important to state that the order in which resilience features are registered dictates the execution order.
 * <br>
 * As an example, in the sample code below, {@code rateLimiter} in {@code moccaResilience1} will be executed before {@code circuitBreaker}. In other words, even if the circuit is open, the rate of calls will still be controlled and limited. However, in {@code moccaResilience2}, once the circuit is open, rate limiting will not be executed.
 * <pre><code>
 * MoccaResilience4j moccaResilience1 = new MoccaResilience4j.Builder()
 *     .rateLimiter(rateLimiter)
 *     .circuitBreaker(circuitBreaker)
 *     .build();
 *
 * MoccaResilience4j moccaResilience2 = new MoccaResilience4j.Builder()
 *     .circuitBreaker(circuitBreaker)
 *     .rateLimiter(rateLimiter)
 *     .build();
 * </code></pre>
 * Notice the instantiation and configuration of {@code rateLimiter} and {@code circuitBreaker} were omitted for brevity.
 *
 * @author crankydillo@gmail.com amansingh21197@gmail.com
 */
public final class MoccaResilience4j extends MoccaResiliency {

    private MoccaResilience4j(final MoccaFeignDecorators decorators) {
        super(Resilience4jFeign.builder(decorators));
    }

    public static class Builder {
        private FeignDecorators.Builder feignBuilder;

        Builder(final FeignDecorators.Builder feignBuilder) {
            this.feignBuilder = feignBuilder;
        }

        public Builder() {
            this(FeignDecorators.builder());
        }

        public MoccaResilience4j build() {
            return new MoccaResilience4j(new MoccaFeignDecorators(feignBuilder.build()));
        }

        /**
         * Adds a {@link Retry} to the decorator chain.
         *
         * @param retry the retry object to be set in this builder
         * @return this builder
         */
        public Builder retry(final Retry retry) {
            this.feignBuilder = feignBuilder.withRetry(retry);
            return this;
        }

        /**
         * Adds a {@link CircuitBreaker} to the decorator chain.
         *
         * @param circuitBreaker the circuit breaker object to be set in this builder
         * @return this builder
         */
        public Builder circuitBreaker(final CircuitBreaker circuitBreaker) {
            this.feignBuilder = feignBuilder.withCircuitBreaker(circuitBreaker);
            return this;
        }

        /**
         * Adds a {@link RateLimiter} to the decorator chain.
         *
         * @param rateLimiter the rate limiter object to be set in this builder
         * @return this builder
         */
        public Builder rateLimiter(final RateLimiter rateLimiter) {
            this.feignBuilder = feignBuilder.withRateLimiter(rateLimiter);
            return this;
        }

        /**
         * Adds a {@link Bulkhead} to the decorator chain.
         *
         * @param bulkhead the bulkhead object to be set in this builder
         * @return this builder
         */
        public Builder bulkhead(final Bulkhead bulkhead) {
            this.feignBuilder = feignBuilder.withBulkhead(bulkhead);
            return this;
        }

        /**
         * Adds a Fallback to the decorator chain
         *
         * @param fallBack the fallback object to be set to in this builder
         * @return this builder
         */
        public Builder fallback(final Object fallBack) {
            this.feignBuilder = feignBuilder.withFallback(fallBack);
            return this;
        }

        /**
         * Adds a Fallback to the decorator chain using the exception filter predicate
         *
         * @param fallBack the fallback object to be set to in this builder
         * @param filter predicate for the exception filter
         * @return this builder
         */
        public Builder fallback(final Object fallBack, Predicate<Exception> filter) {
            this.feignBuilder = feignBuilder.withFallback(fallBack, filter);
            return this;
        }

        /**
         * Adds a Fallback to the decorator chain using the exception filter class
         *
         * @param fallBack the fallback object to be set to in this builder
         * @param filter class for the exception filter
         * @return this builder
         */
        public Builder fallback(final Object fallBack, Class<? extends Exception> filter) {
            this.feignBuilder = feignBuilder.withFallback(fallBack, filter);
            return this;
        }

        /**
         * Adds a Fallback factory to the decorator chain
         *
         * @param fallBackFactory the fallback factory function to be set in this builder
         * @return this builder
         */
        public Builder fallbackFactory(Function<Exception, ?> fallBackFactory ) {
            this.feignBuilder = feignBuilder.withFallbackFactory(fallBackFactory);
            return this;
        }

        /**
         * Adds a Fallback factory to the decorator chain using the exception filter class
         *
         * @param fallbackFactory the fallback factory function to be set in this builder
         * @param filter class for the exception filter
         * @return this builder
         */
        public Builder fallbackFactory(Function<Exception, ?> fallbackFactory,
                                       Class<? extends Exception> filter) {
            this.feignBuilder = feignBuilder.withFallbackFactory(fallbackFactory, filter);
            return this;
        }

        /**
         * Adds a Fallback factory to the decorator chain using the exception filter predicate
         *
         * @param fallbackFactory the fallback factory function to be set in this builder
         * @param filter predicate for the exception filter
         * @return this builder
         */
        public Builder fallbackFactory(Function<Exception, ?> fallbackFactory,
                                       Predicate<Exception> filter) {
            this.feignBuilder = feignBuilder.withFallbackFactory(fallbackFactory, filter);
            return this;
        }

    }
}
