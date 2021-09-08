package com.paypal.mocca.client;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.testng.annotations.Test;

@Test
public class BasicTest extends BasicMoccaHttpClientTest {
    public BasicTest() {
        super(new MoccaApache5Client(HttpClientBuilder.create().build()));
    }
}
