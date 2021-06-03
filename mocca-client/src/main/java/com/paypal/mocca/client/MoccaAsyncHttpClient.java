package com.paypal.mocca.client;

import feign.AsyncClient;

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
 * @param <T> the asynchronous HTTP client type
 * @author fabiocarvalho777@gmail.com
 */
abstract class MoccaAsyncHttpClient<T> {

    private final AsyncClient<T> feignAsyncClient;

    protected MoccaAsyncHttpClient(AsyncClient<T> feignAsyncClient) {
        this.feignAsyncClient =
            Arguments.requireNonNull(feignAsyncClient, "Feign async client cannot be null");
    }

    /**
     * Returns a Feign client containing the asynchronous HTTP client specified in the subclass,
     * to be used in a Mocca builder when creating a Mocca client
     *
     * @return a Feign client containing the asynchronous HTTP client specified in the subclass,
     * to be used in a Mocca builder when creating a Mocca client
     */
    AsyncClient<T> getFeignAsyncClient() {
        return feignAsyncClient;
    }

}