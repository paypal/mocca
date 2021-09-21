package com.paypal.mocca.client;

import com.paypal.mocca.client.sample.SampleClient;
import com.paypal.mocca.client.sample.SampleRequestDTO;
import com.paypal.mocca.client.sample.SampleResponseDTO;
import feign.codec.DecodeException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.*;

public class MoccaClientMutationTest {

    private SampleClient client;

    @BeforeClass
    private void setup() throws IOException {
        String serverBaseUrl = WireMockProvider.startServer();
        client = MoccaClient.Builder.sync(serverBaseUrl).defaultClient().build(SampleClient.class);
    }

    @AfterClass
    public void teardown() {
        WireMockProvider.stopServer();
    }

    @Test
    public void mutationTest() {
        SampleResponseDTO result = client.addSample("boo", "far");
        assertNotNull(result);
        assertEquals(result.getFoo(), "boo");
        assertEquals(result.getBar(), "far");
    }

    @Test
    public void mutationVoidReturnTest() {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("moo", "czar 100%");
        client.addSample(sampleRequestDTO);
    }

    @Test
    public void mutationNoDataTest() {
        SampleResponseDTO result = client.addSample("moo", "czar 100%");
        assertNull(result);
    }

    @Test(expectedExceptions = DecodeException.class, expectedExceptionsMessageRegExp = "(Internal Server Error\\(s\\) while executing query)")
    public void mutationErrorTest() {
        client.addSample("zoo", "car");
    }

    @Test
    public void mutationListTest() {
        List<SampleResponseDTO> sampleReponseDTOS = client.addSampleReturnList("boo", "far");
        assertNotNull(sampleReponseDTOS);
        assertEquals(sampleReponseDTOS.size(), 2);
        assertEquals(sampleReponseDTOS.get(0).getFoo(), "boo1");
        assertEquals(sampleReponseDTOS.get(0).getBar(), "far1");
        assertEquals(sampleReponseDTOS.get(1).getFoo(), "boo2");
        assertEquals(sampleReponseDTOS.get(1).getBar(), "far2");
    }

    @Test
    public void mutationListNoDataTest() {
        List<SampleResponseDTO> sampleReponseDTOS = client.addSampleReturnList("moo", "czar");
        assertNotNull(sampleReponseDTOS);
        assertEquals(sampleReponseDTOS.size(), 0);
    }

}
