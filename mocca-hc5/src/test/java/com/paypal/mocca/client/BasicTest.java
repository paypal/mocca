package com.paypal.mocca.client;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.testng.annotations.Test;

@Test
public class BasicTest extends BasicMoccaHttpClientTest.WithRequestTimeouts {

    @Override
    MoccaHttpClient.WithRequestTimeouts create() {
        return new MoccaApache5Client(HttpClientBuilder.create().build());
    }
}
