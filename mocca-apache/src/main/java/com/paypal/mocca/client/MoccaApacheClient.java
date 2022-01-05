package com.paypal.mocca.client;

import feign.httpclient.ApacheHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Mocca Apache HTTP client. In order to use a Apache HTTP client with Mocca,
 * create a new instance of this class and pass it to Mocca builder.
 * <br>
 * See {@link com.paypal.mocca.client.MoccaClient.Builder.SyncBuilder#client(WithRequestTimeouts)} for further information and code example.
 *
 * @author fabiocarvalho777@gmail.com
 */
final public class MoccaApacheClient extends MoccaHttpClient.WithRequestTimeouts {

    /**
     * Creates a new Mocca Apache HTTP client using
     * default Apache HTTP client configuration
     */
    public MoccaApacheClient() {
        this(HttpClientBuilder.create().build());
    }

    /**
     * Creates a new Mocca Apache HTTP client using
     * a pre-instantiated Apache HTTP client with user
     * defined configuration
     *
     * @param httpClient  a pre-instantiated Apache HTTP client
     *                      with user defined configuration
     */
    public MoccaApacheClient(HttpClient httpClient) {
        super(new ApacheHttpClient(httpClient));
    }

}