package com.paypal.mocca.client;

import feign.jaxrs2.JAXRSClient;

import javax.ws.rs.client.ClientBuilder;

/**
 * Mocca JAXRS2 client. In order to use a JAX-RS 2 client with Mocca,
 * create a new instance of this class and pass it to Mocca builder.
 * <br>
 * See {@link com.paypal.mocca.client.MoccaClient.Builder.SyncBuilder#client(MoccaHttpClient)} for further information and code example.
 */
final public class MoccaJaxrsClient extends MoccaHttpClient {

    /**
     * Create a Mocca JAX-RS 2 client using the supplied {@link ClientBuilder}
     *
     * @param clientBuilder a JAX-RS client builder with user defined configuration
     */
    public MoccaJaxrsClient(final ClientBuilder clientBuilder) {
        super(new JAXRSClient(clientBuilder));
    }
}