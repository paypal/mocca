package com.paypal.mocca.client;

import feign.hc5.AsyncApacheHttp5Client;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;

/**
 * Mocca Async Apache 5 HTTP client. In order to use a Async Apache 5 HTTP client with Mocca,
 * create a new instance of this class and pass it to Mocca builder.
 * <br>
 * See {@link com.paypal.mocca.client.MoccaClient.Builder.AsyncBuilder#client(MoccaAsyncHttpClient)} for further information and code example.
 *
 * @author fabiocarvalho777@gmail.com
 */
final public class MoccaAsyncApache5Client extends MoccaAsyncHttpClient<HttpClientContext> {

    /**
     * Creates a new Mocca Async Apache 5 HTTP client using
     * default Async Apache 5 HTTP client configuration
     */
    public MoccaAsyncApache5Client() {
        this(createStartedClient());
    }

    private static CloseableHttpAsyncClient createStartedClient() {
        final CloseableHttpAsyncClient client = HttpAsyncClients.custom().build();
        client.start();
        return client;
    }

    /**
     * Creates a new Mocca Async Apache 5 HTTP client using
     * a pre-instantiated Async Apache 5 HTTP client with user
     * defined configuration
     *
     * @param httpClient  a pre-instantiated Async Apache 5 HTTP client
     *                      with user defined configuration
     */
    public MoccaAsyncApache5Client(CloseableHttpAsyncClient httpClient) {
        super(new AsyncApacheHttp5Client(httpClient));
    }

}