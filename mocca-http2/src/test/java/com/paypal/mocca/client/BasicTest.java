package com.paypal.mocca.client;

import org.testng.annotations.Test;

import java.net.http.HttpClient;

@Test
public class BasicTest extends BasicMoccaHttpClientTest.WithRequestTimeouts {

    @Override
    MoccaHttpClient.WithRequestTimeouts create() {
        return new MoccaHttp2Client(HttpClient.newBuilder().build());
    }
}
