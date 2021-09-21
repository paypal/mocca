package com.paypal.mocca.client;

import feign.Client;

/**
 * An abstract class representing a synchronous HTTP client supported by Mocca. Subclasses are supposed
 * to work as wrappers to HTTP clients, based on composition, and must adhere to
 * the following rules:
 * <ol>
 *     <li>Be delivered on its own module, named with pattern {@code mocca-<HTTP client name>}</li>
 *     <li>Be packaged at the same package as this abstract class</li>
 *     <li>Be declared as public and final</li>
 *     <li>Offer a public default constructor, providing a default instance of its HTTP client</li>
 *     <li>Offer a public constructor with one argument, allowing users to specify a pre-instantiated custom HTTP client object</li>
 * </ol>
 *
 * @author fabiocarvalho777@gmail.com
 */
abstract class MoccaHttpClient {

    private final Client feignClient;

    // private + extensions = pseudo sealed case class in Java 8 and below.  Modeled after Optional.
    private MoccaHttpClient(Client feignClient) {
        this.feignClient =
            Arguments.requireNonNull(feignClient, "Feign client cannot be null");
    }

    /**
     * This is a 'marker interface'
     */
    abstract static class WithRequestTimeouts extends MoccaHttpClient {
        public WithRequestTimeouts(Client feignClient) {
            super(feignClient);
        }
    }

    abstract static class WithoutRequestTimeouts extends MoccaHttpClient {
        public WithoutRequestTimeouts(Client feignClient) {
            super(feignClient);
        }
    }

    /**
     * Returns a Feign client containing the HTTP synchronous client specified in the subclass,
     * to be used in a Mocca builder when creating a Mocca client
     *
     * @return a Feign client containing the HTTP synchronous client specified in the subclass,
     * to be used in a Mocca builder when creating a Mocca client
     */
    Client getFeignClient() {
        return feignClient;
    }

}
