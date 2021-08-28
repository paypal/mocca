package com.paypal.mocca.client;

import org.testng.annotations.Test;

public class BasicTest extends BasicMoccaHttpClientTest {
    public BasicTest() {
        super(new MoccaOkHttpClient());
    }

    @Test
    void notatest() {
        // TODO figure out why I need this..
    }
}
