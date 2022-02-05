package com.paypal.mocca.client;

import feign.AsyncClient;

import java.time.Duration;

/**
 * An abstract class representing an asynchronous HTTP client supported by Mocca. Subclasses are supposed
 * to work as wrappers to asynchronous HTTP clients, based on composition, and must adhere to
 * the following rules:
 * <ol>
 *     <li>Be delivered on its own module, named with pattern {@code mocca-<HTTP client name>}</li>
 *     <li>Be packaged at the same package as this abstract class</li>
 *     <li>Be declared as public and final</li>
 *     <li>Offer a public default constructor, providing a default instance of its asynchronous HTTP client</li>
 *     <li>Define the generics parameter using the asynchronous HTTP client type</li>
 * </ol>
 *
 * @param <C> an optional context
 * @author fabiocarvalho777@gmail.com
 */
abstract class MoccaAsyncHttpClient<C> {

    private final AsyncClient<C> feignAsyncClient;

    private MoccaAsyncHttpClient(final AsyncClient<C> feignAsyncClient) {
        this.feignAsyncClient =
            Arguments.requireNonNull(feignAsyncClient, "Feign async client cannot be null");
    }

    /**
     * This is a marker interface that signals to {@link MoccaClient.Builder.AsyncBuilder#client(WithRequestTimeouts)}
     * that the supplied {@link AsyncClient} can be used with Mocca-specified timeouts, {@link MoccaClient.Builder.AsyncBuilder.WithRequestTimeouts#options(Duration, Duration, boolean)}.
     */
    abstract static class WithRequestTimeouts<C> extends MoccaAsyncHttpClient<C> {
        public WithRequestTimeouts(final AsyncClient<C> feignClient) {
            super(feignClient);
        }
    }

    /**
     * This is a marker interface that signals to {@link MoccaClient.Builder.AsyncBuilder#client(WithoutRequestTimeouts)}
     * that the supplied {@link AsyncClient} cannot be used with Mocca-specified timeouts.  Timeouts should be
     * handled by the supplied client.
     */
    abstract static class WithoutRequestTimeouts<C> extends MoccaAsyncHttpClient<C> {
        public WithoutRequestTimeouts(final AsyncClient<C> feignClient) {
            super(feignClient);
        }
    }

    /**
     * Returns a Feign client containing the asynchronous HTTP client specified in the subclass,
     * to be used in a Mocca builder when creating a Mocca client
     *
     * @return a Feign client containing the asynchronous HTTP client specified in the subclass,
     * to be used in a Mocca builder when creating a Mocca client
     */
    AsyncClient<C> getFeignAsyncClient() {
        return feignAsyncClient;
    }

}