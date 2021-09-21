package com.paypal.mocca.client;

import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.RequestHeader;
import com.paypal.mocca.client.annotation.RequestHeaderParam;
import feign.RetryableException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Optional;
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
abstract class BasicMoccaHttpClientTest {
    // TODO solve the gradlew problem described above.  Some links:
    // https://discuss.gradle.org/t/testng-tests-that-inherit-from-a-base-class-but-do-not-add-new-test-methods-are-not-detected/1259
    // https://stackoverflow.com/questions/64087969/testng-cannot-find-test-methods-with-inheritance

    static final String GRAPHQL_GREETING = "Hello!";
    static final String RESPONSE_DELAY_HEADER = "ResponseDelay";

    private Server graphqlServer;

    private BasicMoccaHttpClientTest() {
    }

    abstract MoccaHttpClient create();

    abstract static class WithRequestTimeouts extends BasicMoccaHttpClientTest {
        @Override
        abstract MoccaHttpClient.WithRequestTimeouts create();

        @Test(
            description = "GraphQL call respects Mocca-builder specified HTTP read timeout (i.e. per request timeout)."
        )
        void testReadTimeout() {
            final boolean followRedirects = false;
            final Duration connectTimeout = Duration.ofSeconds(5);
            final Duration readTimeout    = Duration.ofMillis(100);

            final SampleDataClient sampleClient = syncBuilder()
                .client(create())
                .options(connectTimeout, readTimeout, followRedirects)
                .build(SampleDataClient.class);
            try {
                sampleClient.greeting(readTimeout.multipliedBy(2).toMillis());
                fail("Expected some form of timeout exception to be thrown.");
            } catch (final RetryableException e) {
                // TODO this needs the same treatment as WithoutRequestTimeouts
                assertEquals(e.getCause().getClass(), SocketTimeoutException.class);
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

    @BeforeClass
    public void setUp() throws Exception {
        final int port = 0; // signals use random port
        final InetSocketAddress addr = new InetSocketAddress("localhost", port);
        graphqlServer = new Server(addr);

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        graphqlServer.setHandler(context);

        final Servlet greetingServlet = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                Optional.ofNullable(req.getHeader(RESPONSE_DELAY_HEADER))
                    .map(Long::parseLong)
                    .ifPresent(delay -> {
                        try {
                            Thread.sleep(delay);
                        } catch (final InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    });

                resp.setStatus(200);
                resp.getWriter().write("{ \"data\": { \"greeting\": \"" + GRAPHQL_GREETING + "\" } }");
            }
        };

        context.addServlet(new ServletHolder(greetingServlet),"/*");

        graphqlServer.start();
    }

    @AfterClass
    public void tearDown() throws Exception {
        graphqlServer.stop();
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
