package com.paypal.mocca.client;

import com.paypal.mocca.client.sample.AsyncSampleClient;
import com.paypal.mocca.client.sample.SampleClient;
import com.paypal.mocca.client.sample.SampleRequestDTO;
import com.paypal.mocca.client.sample.SampleResponseDTO;
import feign.codec.DecodeException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class MoccaClientQueryTest {

    private SampleClient client;
    private String serverBaseUrl;

    @BeforeClass
    private void setup() throws IOException {
        serverBaseUrl = WireMockProvider.startServer();
        client = MoccaClient.Builder.sync(serverBaseUrl).build(SampleClient.class);
    }

    @AfterClass
    public void teardown() {
        WireMockProvider.stopServer();
    }

    @Test
    public void queryTest() {
        String queryVariables = "bar: \"far\", foo: \"boo\"";
        SampleResponseDTO result = client.getOneSample(queryVariables);
        assertNotNull(result);
        assertEquals(result.getFoo(), "boo");
        assertEquals(result.getBar(), "far");
    }

    @Test
    public void queryDtoTest() {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("boo", "far");
        SampleResponseDTO result = client.getOneSample(sampleRequestDTO);
        assertNotNull(result);
        assertEquals(result.getFoo(), "boo");
        assertEquals(result.getBar(), "far");
    }

    @Test
    public void queryDtoWithIgnoreTest() {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("boo", "far");
        SampleResponseDTO result = client.getOneSampleWithIgnore(sampleRequestDTO);
        assertNotNull(result);
        assertNull(result.getFoo());
        assertEquals(result.getBar(), "far");
    }

    @Test
    public void queryNoDataTest() {
        String queryVariables = "foo: \"moo\", bar: \"czar\"";
        SampleResponseDTO result = client.getOneSample(queryVariables);
        assertNull(result);
    }

    @Test
    public void queryNoParamsTest() {
        // FIXME Passing a blank String, as seen below, is a workaround.
        //  It should be allowed to not pass any parameters when the query does not allow any variables.
        //  However, that is not supported at the moment because Feign does not call the registered encoder
        //  if the request method does not have any parameter
        SampleResponseDTO result = client.getOneSample("");
        assertNotNull(result);
        assertEquals(result.getFoo(), "boo");
        assertEquals(result.getBar(), "far");
    }

    @Test(expectedExceptions = DecodeException.class, expectedExceptionsMessageRegExp = "(Internal Server Error\\(s\\) while executing query)")
    public void queryErrorTest() {
        String queryVariables = "foo: \"zoo\", bar: \"car\"";
        client.getOneSample(queryVariables);
    }

    @Test
    public void queryListTest() {
        String queryVariables = "foo: \"boo\", bar: \"far\"";
        List<SampleResponseDTO> sampleResponseDTOS = client.getSamplesList(queryVariables);
        assertNotNull(sampleResponseDTOS);
        assertEquals(sampleResponseDTOS.size(), 2);
        assertEquals(sampleResponseDTOS.get(0).getFoo(), "boo1");
        assertEquals(sampleResponseDTOS.get(0).getBar(), "far1");
        assertEquals(sampleResponseDTOS.get(1).getFoo(), "boo2");
        assertEquals(sampleResponseDTOS.get(1).getBar(), "far2");
    }

    @Test
    public void queryListNoDataTest() {
        String queryVariables = "foo: \"moo\", bar: \"czar\"";
        List<SampleResponseDTO> sampleResponseDTOS = client.getSamplesList(queryVariables);
        assertNotNull(sampleResponseDTOS);
        assertEquals(sampleResponseDTOS.size(), 0);
    }

    @Test(expectedExceptions = DecodeException.class, expectedExceptionsMessageRegExp = "(Internal Server Error\\(s\\) while executing query)")
    public void queryListErrorTest() {
        String queryVariables = "foo: \"zoo\", bar: \"car\"";
        client.getSamplesList(queryVariables);
    }

    @Test
    public void queryCustomSelectionSet() {
        String queryVariables = "bar: \"far\", foo: \"boo\"";
        SampleResponseDTO result = client.getOneSampleCustomSelectionSet(queryVariables);
        assertNotNull(result);
        assertEquals(result.getFoo(), "boo");
        assertNull(result.getBar());
    }

    @Test
    public void queryAsyncTest() throws Exception {
        final String queryVariables = "bar: \"far\", foo: \"boo\"";
        final AsyncSampleClient asyncClient =
                MoccaClient.Builder.async(serverBaseUrl).build(AsyncSampleClient.class);
        final SampleResponseDTO result = asyncClient.getOneSample(queryVariables).get(5, TimeUnit.SECONDS);
        assertEquals(result.getFoo(), "boo");
        assertEquals(result.getBar(), "far");
    }

    @Test(dataProvider = "method-supplier")
    public void testHeaders(Supplier<SampleResponseDTO> method) {
        SampleResponseDTO result = method.get();
        assertNotNull(result);
        assertEquals(result.getFoo(), "boo");
        assertEquals(result.getBar(), "far");
    }

    @DataProvider(name = "method-supplier")
    public Object[][] methodSupplier() {
        String queryVariables = "bar: \"far\", foo: \"boo\"";
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("boo", "far");
        Supplier<SampleResponseDTO> headerAndDTO = () -> client.getOneSampleWithHeaderAndDTO("testvalue", sampleRequestDTO);
        Supplier<SampleResponseDTO> singleHeader = () -> client.getOneSampleWithSingleHeader(queryVariables);
        Supplier<SampleResponseDTO> singleHeaderMultipleVal = () -> client.getOneSampleWithSingleHeaderMultipleValues(queryVariables);
        Supplier<SampleResponseDTO> multipleHeaders = () -> client.getOneSampleWithMultipleHeaders(queryVariables);
        Supplier<SampleResponseDTO> dynamicHeaders = () -> client.getOneSampleWithDynamicHeader("testvalue", queryVariables);
        Supplier<SampleResponseDTO> overrideHeaders = () -> client.getOneSampleWithOverrideClassHeader(queryVariables);
        Supplier<SampleResponseDTO> multipleDynamicHeader = () -> client.getOneSampleWithMultipleDynamicHeaders(
                "testvalue1", "testvalue2", queryVariables);
        Supplier<SampleResponseDTO> staticAndDynamicHeader = () -> client.getOneSampleWithStaticAndDynamicHeaders("bar", queryVariables);
        return new Object[][]{
                {singleHeader},
                {singleHeaderMultipleVal},
                {multipleHeaders},
                {dynamicHeaders},
                {overrideHeaders},
                {headerAndDTO},
                {multipleDynamicHeader},
                {staticAndDynamicHeader}
        };
    }
}
