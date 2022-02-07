package com.paypal.mocca.client;

import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.RequestHeader;
import com.paypal.mocca.client.annotation.RequestHeaderParam;
import feign.AsyncClient;
import org.testng.annotations.Test;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Similar to {@link BasicMoccaHttpClientTest} but for {@link MoccaAsyncHttpClient}.
 */
abstract class BasicMoccaAsyncHttpClientTest extends WithGraphQLServer {
    // Some copying done to keep 'complexity' down in test.

    // And this is what happens when you copy:)
    // TODO solve the gradlew problem described above.  Some links:
    // https://discuss.gradle.org/t/testng-tests-that-inherit-from-a-base-class-but-do-not-add-new-test-methods-are-not-detected/1259
    // https://stackoverflow.com/questions/64087969/testng-cannot-find-test-methods-with-inheritance

    static final String GRAPHQL_GREETING = "Hello!";
    static final String RESPONSE_DELAY_HEADER = "ResponseDelay";

    private BasicMoccaAsyncHttpClientTest() {
    }

    abstract MoccaAsyncHttpClient create();

    abstract static class WithRequestTimeouts extends BasicMoccaAsyncHttpClientTest {

        @Override
        abstract MoccaAsyncHttpClient.WithRequestTimeouts create();

        /**
         * In read timeout scenarios that feign manages, it is expected that the underlying
         * client's timeout exception is conveyed as the first cause.  Each underlying client
         * can have a different class for that and that is what we will assert on.  This defaults
         * to {@link SocketTimeoutException} class, but underlying clients can customize that via
         * an override.
         */
        protected Class<?> expectedTimeoutExceptionCause() {
            return SocketTimeoutException.class;
        }

        @Test(
            description = "GraphQL call respects Mocca-builder specified HTTP read timeout (i.e. per request timeout)."
        )
        void testReadTimeout() throws ExecutionException, InterruptedException, TimeoutException {
            final boolean followRedirects = false;
            final Duration connectTimeout = Duration.ofSeconds(5);
            final Duration readTimeout    = Duration.ofMillis(100);

            final SampleDataClient sampleClient = asyncBuilder()
                .client(create())
                .options(connectTimeout, readTimeout, followRedirects)
                .build(SampleDataClient.class);
            try {
                sampleClient.greeting(readTimeout.multipliedBy(10000).toMillis())
                    .get(5, TimeUnit.SECONDS);
                fail("Expected some form of timeout exception to be thrown.");
            } catch (final ExecutionException ee) {
                assertEquals(ee.getCause().getClass(), expectedTimeoutExceptionCause());
            }
        }
    }

    abstract static class WithoutRequestTimeouts extends BasicMoccaAsyncHttpClientTest {
        @Override
        abstract MoccaAsyncHttpClient.WithoutRequestTimeouts create();

        /**
         * @param readTimeout The maximum time to wait while waiting to read <i>a</i>
         *                    byte from the HTTP response.
         * @return
         */
        abstract <C> TimeoutCollateral<C> create(final Duration readTimeout);

        @Test(
            description = "GraphQL call respects HTTP read timeout."
        )
        void testReadTimeout() {
            final Duration readTimeout = Duration.ofMillis(100);
            final TimeoutCollateral<?> timeoutCollateral = create(readTimeout);
            final SampleDataClient sampleClient = createClient(timeoutCollateral.client);
            try {
                sampleClient.greeting(readTimeout.multipliedBy(2).toMillis());
                fail("Expected some form of timeout exception to be thrown.");
            } catch (final Exception e) {
                timeoutCollateral.exceptionAsserter.accept(e);
            }
        }

        /**
         * Data we need to test HTTP timeout scenarios (e.g. max time between reading bytes).
         */
        static class TimeoutCollateral<C> {
            /**
             * Client that will be used to make the request.
             */
            final MoccaAsyncHttpClient client;

            /**
             * Each implementation is responsible for determining if the thrown exception
             * has the appropriate state.  For this, they provide this 'asserting' function.
             */
            final Consumer<Exception> exceptionAsserter;

            TimeoutCollateral(final MoccaAsyncHttpClient client,
                              final Consumer<Exception> exceptionAsserter) {
                this.client = client;
                this.exceptionAsserter = exceptionAsserter;
            }
        }
    }

    @Test(description = "Basic GraphQL call")
    void testBasic() throws Exception {
        final String greeting = createClient().greeting().get(5, TimeUnit.SECONDS);
        assertEquals(greeting, GRAPHQL_GREETING);
    }

    protected SampleDataClient createClient() {
        return createClient(create());
    }

    protected SampleDataClient createClient(final MoccaAsyncHttpClient httpClient) {
        // This is a little bit hacky, but the general idea is the tests that leverage
        // this function are not concerned with setting timeouts.
        class MyClient extends MoccaAsyncHttpClient.WithoutRequestTimeouts {
            public MyClient(AsyncClient<?> feignClient) {
                super(feignClient);
            }
        }

        final MoccaAsyncHttpClient.WithoutRequestTimeouts clientWithoutTimeouts =
            new MyClient(httpClient.getFeignAsyncClient());

        return asyncBuilder()
                .client(clientWithoutTimeouts)
                .build(SampleDataClient.class);
    }

    protected MoccaClient.Builder.AsyncBuilder asyncBuilder() {
        return MoccaClient.Builder.async(graphqlServer.getURI().toASCIIString());
    }

    public interface SampleDataClient extends MoccaClient {
        @Query
        CompletableFuture<String> greeting();

        /**
         * A delayed greeting.
         *
         * @param delayInMs The amount of time the server should wait before replying
         */
        @Query
        @RequestHeader(RESPONSE_DELAY_HEADER + ": {delay}")
        CompletableFuture<String> greeting(@RequestHeaderParam("delay") long delayInMs);
    }
}
