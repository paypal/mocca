package com.paypal.mocca.client;

/**
 * Mocca OkHttp client. In order to use a OkHttp client with Mocca,
 * create a new instance of this class and pass it to Mocca builder.
 * <br>
 * See {@link com.paypal.mocca.client.MoccaClient.Builder.SyncBuilder#client(MoccaHttpClient)} for further information and code example.
 *
 * @author fabiocarvalho777@gmail.com
 */
final public class MoccaOkHttpClient extends MoccaHttpClient {

    /**
     * Creates a new Mocca OkHttp client using
     * default OkHttp client configuration
     */
    public MoccaOkHttpClient() {
        this(new okhttp3.OkHttpClient());
    }

    /**
     * Creates a new Mocca OkHttp client using
     * a pre-instantiated OkHttp client with user
     * defined configuration
     *
     * @param okHttpClient  a pre-instantiated OkHttp client
     *                      with user defined configuration
     */
    public MoccaOkHttpClient(okhttp3.OkHttpClient okHttpClient) {
        super(new feign.okhttp.OkHttpClient(okHttpClient));
    }

}