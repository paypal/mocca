package com.paypal.mocca.client;

import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.Test;

@Test
public class BasicTest extends BasicMoccaHttpClientTest.WithRequestTimeouts {

    @Override
    MoccaHttpClient.WithRequestTimeouts create() {
        return new MoccaApacheClient(HttpClientBuilder.create().build());
    }

    @Test(
            description = "GraphQL call respects client instance specified HTTP read timeout (i.e. per request timeout)."
    )
    void testClientInstanceReadTimeout() {
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(testClientInstanceReadTimeout_CLIENT_READ_TIMEOUT)
                .build();

        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultSocketConfig(socketConfig).build();

        testClientInstanceReadTimeout(new MoccaApacheClient(httpClient));
    }

}