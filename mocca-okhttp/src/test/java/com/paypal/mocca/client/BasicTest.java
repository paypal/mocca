package com.paypal.mocca.client;

import org.testng.annotations.Test;

@Test
public class BasicTest extends BasicMoccaHttpClientTest {
    public BasicTest() {
        super(new MoccaOkHttpClient());
    }
}
