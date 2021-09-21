package com.paypal.mocca.client;

import com.google.api.client.http.javanet.NetHttpTransport;
import org.testng.annotations.Test;

import java.time.Duration;

@Test
public class BasicTest extends BasicMoccaHttpClientTest.WithRequestTimeouts {

    @Override
    MoccaHttpClient.WithRequestTimeouts create() {
        return new MoccaGoogleHttpClient(new NetHttpTransport());
    }
}
