package com.paypal.mocca.client;

import feign.jaxrs2.JAXRSClient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;
import java.security.KeyStore;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Mocca JAXRS2 client. In order to use a JAX-RS 2 client with Mocca, create a new instance of this class and pass it to
 * Mocca builder.
 * <br>
 * See {@link com.paypal.mocca.client.MoccaClient.Builder.SyncBuilder#client(MoccaHttpClient)} for further information
 * and code example.
 */
final public class MoccaJaxrsClient extends MoccaHttpClient {

    /**
     * Create a Mocca JAX-RS 2 client using the supplied {@link Client}.
     *
     * @param client a JAX-RS client with user defined configuration
     */
    public MoccaJaxrsClient(final Client client) {
        super(new JAXRSClient(new StubbornClientBuilder(client)));
    }

    private static class StubbornClientBuilder extends ClientBuilder {
        private static final String USAGE_ERR_MSG = "Only #build can be called.";

        final Client client;

        private StubbornClientBuilder(final Client client) {
            this.client = Arguments.requireNonNull(client);
        }

        @Override
        public Client build() {
            return client;
        }

        @Override
        public ClientBuilder withConfig(Configuration config) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder sslContext(SSLContext sslContext) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder keyStore(KeyStore keyStore, char[] password) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder trustStore(KeyStore trustStore) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder hostnameVerifier(HostnameVerifier verifier) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder executorService(ExecutorService executorService) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder scheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder connectTimeout(long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder readTimeout(long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public Configuration getConfiguration() {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder property(String name, Object value) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder register(Class<?> componentClass) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder register(Class<?> componentClass, int priority) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder register(Class<?> componentClass, Class<?>... contracts) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder register(Object component) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder register(Object component, int priority) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder register(Object component, Class<?>... contracts) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }

        @Override
        public ClientBuilder register(Object component, Map<Class<?>, Integer> contracts) {
            throw new UnsupportedOperationException(USAGE_ERR_MSG);
        }
    }
}