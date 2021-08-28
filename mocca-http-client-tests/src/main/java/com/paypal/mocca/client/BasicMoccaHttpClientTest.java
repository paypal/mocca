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

abstract class BasicMoccaHttpClientTest {

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
                resp.getWriter().write("Hi!");
            }
        });
        graphqlServer.start();
        sampleDataClient = MoccaClient.Builder.sync(graphqlServer.getURI().toASCIIString())
                .client(Arguments.requireNonNull(moccaHttpClient))
                .build(SampleDataClient.class);
    }

    @AfterClass
    public void tearDown() throws Exception {
        graphqlServer.stop();
    }

    @Test(description = "Basic GraphQL call")
    void testBasic() {
        final String greeting = sampleDataClient.greeting();
        assertEquals(greeting, "hi!");
    }

    public interface SampleDataClient extends MoccaClient {
        @Query
        String greeting();
    }
}
