package com.paypal.mocca.client;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

/**
 * Mocca Google HTTP client. In order to use a Google HTTP client with Mocca,
 * create a new instance of this class and pass it to Mocca builder.
 * <br>
 * See {@link com.paypal.mocca.client.MoccaClient.Builder.SyncBuilder#client(MoccaHttpClient)} for further information and code example.
 *
 * @author fabiocarvalho777@gmail.com
 */
final public class MoccaGoogleHttpClient extends MoccaHttpClient {

    /**
     * Creates a new Mocca Google HTTP client using
     * default Google HTTP client configuration
     */
    public MoccaGoogleHttpClient() {
        this(new NetHttpTransport());
    }

    /**
     * Creates a new Mocca Google HTTP client using
     * a pre-instantiated Google HTTP client with user
     * defined configuration
     *
     * @param httpTransport  a pre-instantiated Google HTTP client
     *                      with user defined configuration
     */
    public MoccaGoogleHttpClient(HttpTransport httpTransport) {
        super(new feign.googlehttpclient.GoogleHttpClient(httpTransport));
    }

}