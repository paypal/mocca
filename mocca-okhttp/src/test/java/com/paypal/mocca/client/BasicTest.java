package com.paypal.mocca.client;

import okhttp3.OkHttpClient;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

@Test
public class BasicTest extends BasicMoccaHttpClientTest.WithRequestTimeouts {

    @Override
    MoccaHttpClient.WithRequestTimeouts create() {
        return new MoccaOkHttpClient();
    }

    @Test(
            description = "GraphQL call respects client instance specified HTTP read timeout (i.e. per request timeout)."
    )
    void testClientInstanceReadTimeout() {
        OkHttpClient httpClient = new OkHttpClient().newBuilder()
                .readTimeout(testClientInstanceReadTimeout_CLIENT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();

        testClientInstanceReadTimeout(new MoccaOkHttpClient(httpClient));
    }

}
