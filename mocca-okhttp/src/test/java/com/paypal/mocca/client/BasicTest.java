package com.paypal.mocca.client;

import org.testng.annotations.Test;

@Test
public class BasicTest extends BasicMoccaHttpClientTest.WithRequestTimeouts {

    @Override
    MoccaHttpClient.WithRequestTimeouts create() {
        return new MoccaOkHttpClient();
    }
}
