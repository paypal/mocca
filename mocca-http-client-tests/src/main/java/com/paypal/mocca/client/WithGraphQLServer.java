package com.paypal.mocca.client;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

class WithGraphQLServer {
    static final String GRAPHQL_GREETING = "Hello!";
    static final String RESPONSE_DELAY_HEADER = "ResponseDelay";

    protected Server graphqlServer;

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
                // TODO Do better with this.  Does that mean we run a full fledged graphql server?
                // Probably.  I mean who says 'Content-Encoding' can't some day be UTF-8..
                final String contentType = req.getHeader(HttpHeader.CONTENT_TYPE.asString());
                if (!"application/json".equalsIgnoreCase(contentType)) {
                    throw new RuntimeException("Content-Type must be application/json, was " + contentType);
                }
                final String contentEncoding = req.getHeader(HttpHeader.CONTENT_ENCODING.asString());
                if ("UTF-8".equalsIgnoreCase(contentEncoding)) {
                    throw new RuntimeException("Content-Encoding cannot be UTF-8");
                }
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
}