package com.paypal.mocca.client;

import org.testng.annotations.Test;

import javax.ws.rs.client.ClientBuilder;

@Test
public class BasicTest extends BasicMoccaHttpClientTest {
    public BasicTest() {
        super(new MoccaJaxrsClient(ClientBuilder.newClient()));
    }
}
