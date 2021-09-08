package com.paypal.mocca.client;

import com.google.api.client.http.javanet.NetHttpTransport;
import org.testng.annotations.Test;

@Test
public class BasicTest extends BasicMoccaHttpClientTest {
    public BasicTest() {
        super(new MoccaGoogleHttpClient(new NetHttpTransport()));
    }
}
