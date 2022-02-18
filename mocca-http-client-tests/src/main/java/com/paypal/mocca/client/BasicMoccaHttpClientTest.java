package com.paypal.mocca.client;

import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.RequestHeader;
import com.paypal.mocca.client.annotation.RequestHeaderParam;
import feign.RetryableException;
import org.testng.annotations.Test;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.function.Consumer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Verifies that a supplied {@link MoccaHttpClient} works for
 * some basic GraphQL call.  The general idea being that we're
 * just testing the basic HTTP parts function correctly (e.g.
 * POST requests).
 * <p></p>
 * Unfortunately, there seems to be a Gradle or TestNG bug where
 * if a class contains no tests but extends one that does, then
 * Gradle build will not execute the test. Annotating the concrete
 * class with `@Test` appears to 'solve' the problem.
 */
abstract class BasicMoccaHttpClientTest<T> extends WithGraphQLServer {
    // TODO solve the gradlew problem described above.  Some links:
    // https://discuss.gradle.org/t/testng-tests-that-inherit-from-a-base-class-but-do-not-add-new-test-methods-are-not-detected/1259
    // https://stackoverflow.com/questions/64087969/testng-cannot-find-test-methods-with-inheritance

    private BasicMoccaHttpClientTest() {
    }

    abstract MoccaHttpClient create();

    abstract static class WithRequestTimeouts extends BasicMoccaHttpClientTest {

        @Override
        abstract MoccaHttpClient.WithRequestTimeouts create();

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
        void testReadTimeout() {
            testClientInstanceReadTimeout(create());
        }

        protected static final int testClientInstanceReadTimeout_CLIENT_READ_TIMEOUT = 5000;

        /**
         * This will perform a test where the GraphQL call respects Mocca builder specified HTTP read timeout.
         * The provided client is intentionally set with a read time out (testClientInstanceReadTimeout_CLIENT_READ_TIMEOUT) bigger
         * than the one set directly with Mocca builder API to show and assert that that will be ignored and overridden by Feign.
         */
        protected void testClientInstanceReadTimeout(MoccaHttpClient.WithRequestTimeouts client) {
            final boolean followRedirects = false;
            final Duration connectTimeout = Duration.ofSeconds(1);
            final Duration readTimeout = Duration.ofMillis(100);
            final Duration greetingsDelayTimeout = Duration.ofMillis(200);

            final SampleDataClient sampleClient = syncBuilder()
                    .client(client)
                    .options(connectTimeout, readTimeout, followRedirects)
                    .build(SampleDataClient.class);
            try {
                sampleClient.greeting(greetingsDelayTimeout.toMillis());
                fail("Expected some form of timeout exception to be thrown.");
            } catch (final MoccaException e) {
                // TODO how does feign know that a request timeout scenario means you can safely
                // retry the request?  If the server has received any of the request, I don't think
                // that's valid.  Consider writing up a feign bug.
                assertEquals(e.getCause().getClass(), RetryableException.class);
                assertEquals(e.getCause().getCause().getClass(), expectedTimeoutExceptionCause());
            }
        }

    }

    abstract static class WithoutRequestTimeouts extends BasicMoccaHttpClientTest {

        @Override
        abstract MoccaHttpClient.WithoutRequestTimeouts create();

        /**
         * @param readTimeout The maximum time to wait while waiting to read <i>a</i>
         *                    byte from the HTTP response.
         * @return
         */
        abstract TimeoutCollateral create(final Duration readTimeout);

        @Test(
            description = "GraphQL call respects HTTP read timeout."
        )
        void testReadTimeout() {
            final Duration readTimeout = Duration.ofMillis(100);
            final TimeoutCollateral timeoutCollateral = create(readTimeout);
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
        static class TimeoutCollateral {
            /**
             * Client that will be used to make the request.
             */
            final MoccaHttpClient client;

            /**
             * Each implementation is responsible for determining if the thrown exception
             * has the appropriate state.  For this, they provide this 'asserting' function.
             */
            final Consumer<Exception> exceptionAsserter;

            TimeoutCollateral(final MoccaHttpClient client,
                              final Consumer<Exception> exceptionAsserter) {
                this.client = client;
                this.exceptionAsserter = exceptionAsserter;
            }
        }
    }


    @Test(description = "Basic GraphQL call")
    void testBasic() {
        final String greeting = createClient().greeting();
        assertEquals(greeting, GRAPHQL_GREETING);
    }

    protected SampleDataClient createClient() {
        return createClient(create());
    }

    protected SampleDataClient createClient(final MoccaHttpClient httpClient) {
        // This is a little bit hacky, but the general idea is the tests that leverage
        // this function are not concerned with setting timeouts.
        final MoccaHttpClient.WithoutRequestTimeouts clientWithoutTimeouts =
            new MoccaHttpClient.WithoutRequestTimeouts(httpClient.getFeignClient()) {};

        return syncBuilder()
                .client(clientWithoutTimeouts)
                .build(SampleDataClient.class);
    }

    protected MoccaClient.Builder.SyncBuilder syncBuilder() {
        return MoccaClient.Builder.sync(graphqlServer.getURI().toASCIIString());
    }

    public interface SampleDataClient extends MoccaClient {
        @Query
        String greeting();

        /**
         * A delayed greeting.
         *
         * @param delayInMs The amount of time the server should wait before replying
         */
        @Query
        @RequestHeader(RESPONSE_DELAY_HEADER + ": {delay}")
        String greeting(@RequestHeaderParam("delay") long delayInMs);
    }
}
