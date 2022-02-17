package com.paypal.mocca.client;

import feign.http2client.Http2Client;

import java.net.SocketTimeoutException;
import java.net.http.HttpClient;

/**
 * Mocca Java 11 HTTP 2 client. In order to use a Java 11 HTTP 2 client with Mocca,
 * create a new instance of this class and pass it to Mocca builder.
 * <br>
 * See {@link com.paypal.mocca.client.MoccaClient.Builder.SyncBuilder#client(WithRequestTimeouts)} for further information and code example.
 *
 * @author fabiocarvalho777@gmail.com
 */
final public class MoccaHttp2Client extends MoccaHttpClient.WithRequestTimeouts {

    /**
     * Creates a new Mocca Java 11 HTTP 2 client using
     * default Java 11 HTTP 2 client configuration
     */
    public MoccaHttp2Client() {
        this(HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .version(HttpClient.Version.HTTP_2)
                .build());
    }

    /**
     * Creates a new Mocca Java 11 HTTP 2 client using
     * a pre-instantiated Java 11 HTTP 2 client with user
     * defined configuration
     *
     * @param httpClient  a pre-instantiated Java 11 HTTP 2 client
     *                      with user defined configuration
     */
    public MoccaHttp2Client(HttpClient httpClient) {
        super(new Http2Client(httpClient));
    }

}