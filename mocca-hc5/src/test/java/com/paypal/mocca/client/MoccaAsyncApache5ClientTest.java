package com.paypal.mocca.client;

import org.testng.annotations.Test;

@Test
public class MoccaAsyncApache5ClientTest extends BasicMoccaAsyncHttpClientTest.WithRequestTimeouts {
    @Override
    MoccaAsyncHttpClient.WithRequestTimeouts create() {
        return new MoccaAsyncApache5Client();
    }
}
