package com.paypal.mocca.client;

import com.paypal.mocca.client.annotation.Query;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

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

    private static final String GRAPHQL_GREETING = "Hello!";

    private final MoccaHttpClient moccaHttpClient;
    private Server graphqlServer;
    private SampleDataClient sampleDataClient;

    BasicMoccaHttpClientTest(MoccaHttpClient moccaHttpClient) {
        this.moccaHttpClient = Arguments.requireNonNull(moccaHttpClient);
    }

    @BeforeClass
    public void setUp() throws Exception {
        final int port = 0; // signals use random port
        graphqlServer = new Server(port);
        graphqlServer.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
                baseRequest.setHandled(true);
                resp.getWriter().write("{ \"data\": { \"greeting\": \"" + GRAPHQL_GREETING + "\" } }");
            }
        });
        graphqlServer.start();
        sampleDataClient = MoccaClient.Builder.sync(graphqlServer.getURI().toASCIIString())
                .client(moccaHttpClient)
                .build(SampleDataClient.class);
    }

    @AfterClass
    public void tearDown() throws Exception {
        graphqlServer.stop();
    }

    @Test(description = "Basic GraphQL call")
    void testBasic() {
        final String greeting = sampleDataClient.greeting();
        assertEquals(greeting, GRAPHQL_GREETING);
    }

    public interface SampleDataClient extends MoccaClient {
        @Query
        String greeting();
    }
}
