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
        client = MoccaClient.Builder.sync(serverBaseUrl).build(SampleClient.class);
    }

    @AfterClass
    public void teardown() {
        WireMockProvider.stopServer();
    }

    @Test
    public void mutationTest() {
        String mutationVariables = "foo: \"boo\", bar: \"far\"";
        SampleResponseDTO result = client.addSample(mutationVariables);
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
        String mutationVariables = "foo: \"moo\", bar: \"czar 100%\"";
        SampleResponseDTO result = client.addSample(mutationVariables);
        assertNull(result);
    }

    @Test(expectedExceptions = DecodeException.class, expectedExceptionsMessageRegExp = "(Internal Server Error\\(s\\) while executing query)")
    public void mutationErrorTest() {
        String mutationVariables = "foo: \"zoo\", bar: \"car\"";
        client.addSample(mutationVariables);
    }

    @Test
    public void mutationListTest() {
        String mutationVariables = "foo: \"boo\", bar: \"far\"";
        List<SampleResponseDTO> sampleReponseDTOS = client.addSampleReturnList(mutationVariables);
        assertNotNull(sampleReponseDTOS);
        assertEquals(sampleReponseDTOS.size(), 2);
        assertEquals(sampleReponseDTOS.get(0).getFoo(), "boo1");
        assertEquals(sampleReponseDTOS.get(0).getBar(), "far1");
        assertEquals(sampleReponseDTOS.get(1).getFoo(), "boo2");
        assertEquals(sampleReponseDTOS.get(1).getBar(), "far2");
    }

    @Test
    public void mutationListNoDataTest() {
        String mutationVariables = "foo: \"moo\", bar: \"czar\"";
        List<SampleResponseDTO> sampleReponseDTOS = client.addSampleReturnList(mutationVariables);
        assertNotNull(sampleReponseDTOS);
        assertEquals(sampleReponseDTOS.size(), 0);
    }

}
