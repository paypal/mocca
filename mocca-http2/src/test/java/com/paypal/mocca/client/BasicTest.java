package com.paypal.mocca.client;

import org.testng.annotations.Test;

import java.net.http.HttpClient;

@Test
public class BasicTest extends BasicMoccaHttpClientTest {
    public BasicTest() {
        super(new MoccaHttp2Client(HttpClient.newBuilder().build()));
    }
}
