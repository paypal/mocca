package com.paypal.mocca.client;

import org.testng.annotations.Test;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

@Test
public class BasicTest extends BasicMoccaHttpClientTest.WithoutRequestTimeouts {
    @Override
    MoccaHttpClient.WithoutRequestTimeouts create() {
        return new MoccaJaxrsClient(ClientBuilder.newClient());
    }

    @Override
    TimeoutCollateral create(final Duration readTimeout) {
        final MoccaHttpClient client = new MoccaJaxrsClient(
            ClientBuilder.newBuilder()
                .readTimeout(readTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .build()
        );
        return new TimeoutCollateral(
            client,
            e -> assertEquals(e.getCause().getClass(), SocketTimeoutException.class)
        );
    }

}
