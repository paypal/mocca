package com.paypal.mocca.functional;

import com.paypal.mocca.client.MoccaClient;
import com.paypal.mocca.client.MoccaOkHttpClient;
import com.paypal.mocca.server.GraphQLEndpoint;
import okhttp3.OkHttpClient;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.jetty.JettyTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;

import javax.ws.rs.core.Application;
import java.util.concurrent.TimeUnit;

/**
 * Abstract functional tests which spins up Jersey container
 */
public abstract class AbstractFunctionalTests extends JerseyTestNg.ContainerPerClassTest {

    /**
     * Mocca GraphQL Client
     */
    protected BooksAppClient client;

    AbstractFunctionalTests() {
        okhttp3.OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(1, TimeUnit.SECONDS)
                .build();

        client = MoccaClient.Builder
                .sync(getBaseUri().toString())
                .connectionTimeout(1000)
                .client(new MoccaOkHttpClient(okHttpClient))
                .build(BooksAppClient.class);
    }

    /**
     * Configures Jersey
     *
     * @return JAX-RS application
     */
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(JacksonFeature.class);
        resourceConfig.register(GraphQLEndpoint.class);
        return resourceConfig;
    }

    /**
     * Test container factory
     *
     * @return TestContainerFactory
     * @throws TestContainerException if Netty cannot be instantiated
     */
    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new JettyTestContainerFactory();
    }
}
