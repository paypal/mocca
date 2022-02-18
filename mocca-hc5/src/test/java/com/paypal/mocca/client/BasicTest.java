package com.paypal.mocca.client;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

@Test
public class BasicTest extends BasicMoccaHttpClientTest.WithRequestTimeouts {

    @Override
    MoccaHttpClient.WithRequestTimeouts create() {
        return new MoccaApache5Client(HttpClientBuilder.create().build());
    }

    @Test(
            description = "GraphQL call respects client instance specified HTTP read timeout (i.e. per request timeout)."
    )
    void testClientInstanceReadTimeout() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setResponseTimeout(testClientInstanceReadTimeout_CLIENT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();

        testClientInstanceReadTimeout(new MoccaApache5Client(httpClient));
    }

}