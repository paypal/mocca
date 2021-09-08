package com.paypal.mocca.client;

import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.Test;

@Test
public class BasicTest extends BasicMoccaHttpClientTest {
    public BasicTest() {
        super(new MoccaApacheClient(HttpClientBuilder.create().build()));
    }
}
