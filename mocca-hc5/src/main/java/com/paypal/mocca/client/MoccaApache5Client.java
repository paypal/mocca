package com.paypal.mocca.client;

import feign.hc5.ApacheHttp5Client;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

/**
 * Mocca Apache 5 HTTP client. In order to use a Apache 5 HTTP client with Mocca,
 * create a new instance of this class and pass it to Mocca builder.
 * <br>
 * See {@link com.paypal.mocca.client.MoccaClient.Builder.SyncBuilder#client(MoccaHttpClient)} for further information and code example.
 *
 * @author fabiocarvalho777@gmail.com
 */
final public class MoccaApache5Client extends MoccaHttpClient {

    /**
     * Creates a new Mocca Apache 5 HTTP client using
     * default Apache 5 HTTP client configuration
     */
    public MoccaApache5Client() {
        this(HttpClientBuilder.create().build());
    }

    /**
     * Creates a new Mocca Apache 5 HTTP client using
     * a pre-instantiated Apache 5 HTTP client with user
     * defined configuration
     *
     * @param httpClient a pre-instantiated Apache 5 HTTP client
     *                      with user defined configuration
     */
    public MoccaApache5Client(HttpClient httpClient) {
        super(new ApacheHttp5Client(httpClient));
    }

}