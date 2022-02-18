package com.paypal.mocca.client;

import com.google.api.client.http.javanet.NetHttpTransport;
import org.testng.annotations.Test;

@Test
public class BasicTest extends BasicMoccaHttpClientTest.WithRequestTimeouts<NetHttpTransport> {

    @Override
    MoccaHttpClient.WithRequestTimeouts create() {
        return new MoccaGoogleHttpClient(new NetHttpTransport());
    }

    @Override
    MoccaHttpClient.WithRequestTimeouts create(NetHttpTransport httpClient) {
        return new MoccaGoogleHttpClient(httpClient);
    }

}