package com.paypal.mocca.client;

import feign.jaxrs2.JAXRSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Mocca JAX-RS 2 client. In order to use a JAX-RS 2 client with Mocca, create a new instance of this class and pass it
 * to Mocca builder.
 * <br>
 * See {@link com.paypal.mocca.client.MoccaClient.Builder.SyncBuilder#client(MoccaHttpClient)} for further information
 * and code example.
 * <br>
 * Instances of this class are technically supposed to support concepts like setting the read and connect timeouts per
 * request.  However, those are ignored.  The only timeouts that are used are what's established in the supplied
 * {@link Client}.
 */
final public class MoccaJaxrsClient extends MoccaHttpClient {
    private static Logger log = LoggerFactory.getLogger(MoccaJaxrsClient.class);

    /**
     * Create a Mocca JAX-RS 2 client using the supplied {@link Client}.
     *
     * @param client a JAX-RS client with user defined configuration
     */
    public MoccaJaxrsClient(final Client client) {
        super(new JAXRSClient(new StubbornClientBuilder(client)));
        log.debug("Users of this may attempt to set read timeout and connect timeout per request.  " +
            "However, those are ignored.  They should be established in the supplied javax.ws.rs.Client.");
    }

    private static class StubbornClientBuilder extends ClientBuilder {
        private static Logger log = LoggerFactory.getLogger(StubbornClientBuilder.class);
        private static final String USAGE_ERR_MSG = "Only #build can be called.";

        private final Client client;

        private StubbornClientBuilder(Client client) {
            this.client = client;
        }

        @Override
        public Client build() {
            return client;
        }

        @Override
        public ClientBuilder withConfig(Configuration config) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder sslContext(SSLContext sslContext) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder keyStore(KeyStore keyStore, char[] password) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder trustStore(KeyStore trustStore) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder hostnameVerifier(HostnameVerifier verifier) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder executorService(ExecutorService executorService) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder scheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder connectTimeout(long timeout, TimeUnit unit) {
            return this;
        }

        @Override
        public ClientBuilder readTimeout(long timeout, TimeUnit unit) {
            return this;
        }

        @Override
        public Configuration getConfiguration() {
            return client.getConfiguration();
        }

        @Override
        public ClientBuilder property(String name, Object value) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder register(Class<?> componentClass) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder register(Class<?> componentClass, int priority) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder register(Class<?> componentClass, Class<?>... contracts) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder register(Object component) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder register(Object component, int priority) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder register(Object component, Class<?>... contracts) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }

        @Override
        public ClientBuilder register(Object component, Map<Class<?>, Integer> contracts) {
            log.warn(USAGE_ERR_MSG);
            return this;
        }
    }
}
