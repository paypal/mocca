package com.paypal.mocca.client;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.net.HttpURLConnection;

/**
 * Mocca HTTP client based on {@link HttpURLConnection},
 * used as the default client when a custom one is not specified by the application.
 *
 * @author fabiocarvalho777@gmail.com
 */
final public class MoccaDefaultHttpClient extends MoccaHttpClient.WithRequestTimeouts {

    /**
     * Creates a new Mocca default HTTP client using
     * default configuration
     */
    public MoccaDefaultHttpClient() {
        this(null, null);
    }

    /**
     * Creates a new Mocca default HTTP client using
     * user defined configuration
     *
     * @param sslContextFactory the SSL context factory to be used with this client
     * @param hostnameVerifier the hostname verifier to be used with this client
     */
    public MoccaDefaultHttpClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
        super(new feign.Client.Default(sslContextFactory, hostnameVerifier));
    }

    /**
     * Creates a new Mocca default HTTP client using
     * user defined configuration
     *
     * @param sslContextFactory the SSL context factory to be used with this client
     * @param hostnameVerifier the hostname verifier to be used with this client
     * @param disableRequestBuffering whether to disable or not request buffering
     */
    public MoccaDefaultHttpClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier, boolean disableRequestBuffering) {
        super(new feign.Client.Default(sslContextFactory, hostnameVerifier, disableRequestBuffering));
    }

}