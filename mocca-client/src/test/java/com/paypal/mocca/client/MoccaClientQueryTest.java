package com.paypal.mocca.client;

import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.RequestHeaderParam;
import com.paypal.mocca.client.annotation.Var;
import com.paypal.mocca.client.sample.AsyncSampleClient;
import com.paypal.mocca.client.sample.SampleClient;
import com.paypal.mocca.client.sample.SampleRequestDTO;
import com.paypal.mocca.client.sample.SampleResponseDTO;
import feign.codec.DecodeException;
import feign.codec.EncodeException;
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
    public void queryRawTest() {
        String queryVariables = "foo: \"boo\", bar: \"far\"";
        SampleResponseDTO result = client.getOneSample(queryVariables);
        assertNotNull(result);
        assertEquals(result.getFoo(), "boo");
        assertEquals(result.getBar(), "far");
    }

    @Test
    public void queryVariableTest() {
        SampleResponseDTO result = client.getOneSample("boo", "far");
        assertNotNull(result);
        assertEquals(result.getFoo(), "boo");
        assertEquals(result.getBar(), "far");
    }

    @Test
    public void queryDtoTest() {
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("boom", "zaz");
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
        SampleResponseDTO result = client.getOneSample("moo", "czar");
        assertNull(result);
    }

    @Test
    public void queryNoParamsTest() {
        SampleResponseDTO result = client.getOneSample();
        assertNotNull(result);
        assertEquals(result.getFoo(), "boo");
        assertEquals(result.getBar(), "far");
    }

    @Test(expectedExceptions = DecodeException.class, expectedExceptionsMessageRegExp = "(Internal Server Error\\(s\\) while executing query)")
    public void queryErrorTest() {
        client.getOneSample("zoo", "car");
    }

    @Test
    public void queryListTest() {
        List<SampleResponseDTO> sampleResponseDTOS = client.getSamplesList("boo", "far");
        assertNotNull(sampleResponseDTOS);
        assertEquals(sampleResponseDTOS.size(), 2);
        assertEquals(sampleResponseDTOS.get(0).getFoo(), "boo1");
        assertEquals(sampleResponseDTOS.get(0).getBar(), "far1");
        assertEquals(sampleResponseDTOS.get(1).getFoo(), "boo2");
        assertEquals(sampleResponseDTOS.get(1).getBar(), "far2");
    }

    @Test
    public void queryListNoDataTest() {
        List<SampleResponseDTO> sampleResponseDTOS = client.getSamplesList("moo", "czar");
        assertNotNull(sampleResponseDTOS);
        assertEquals(sampleResponseDTOS.size(), 0);
    }

    @Test(expectedExceptions = DecodeException.class, expectedExceptionsMessageRegExp = "(Internal Server Error\\(s\\) while executing query)")
    public void queryListErrorTest() {
        client.getSamplesList("zoo", "car");
    }

    @Test
    public void queryCustomSelectionSet() {
        SampleResponseDTO result = client.getOneSampleCustomSelectionSet("boo", "far");
        assertNotNull(result);
        assertEquals(result.getFoo(), "boo");
        assertNull(result.getBar());
    }

    @Test
    public void queryAsyncTest() throws Exception {
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
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("boo", "far");
        Supplier<SampleResponseDTO> headerAndDTO = () -> client.getOneSampleWithHeaderAndDTO("testvalue", sampleRequestDTO);
        Supplier<SampleResponseDTO> singleHeader = () -> client.getOneSampleWithSingleHeader("boo", "far");
        Supplier<SampleResponseDTO> singleHeaderMultipleVal = () -> client.getOneSampleWithSingleHeaderMultipleValues("boo", "far");
        Supplier<SampleResponseDTO> multipleHeaders = () -> client.getOneSampleWithMultipleHeaders("boo", "far");
        Supplier<SampleResponseDTO> dynamicHeaders = () -> client.getOneSampleWithDynamicHeader("testvalue", "boo", "far");
        Supplier<SampleResponseDTO> overrideHeaders = () -> client.getOneSampleWithOverrideClassHeader("boo", "far");
        Supplier<SampleResponseDTO> multipleDynamicHeader = () -> client.getOneSampleWithMultipleDynamicHeaders(
                "testvalue1", "testvalue2", "boo", "far");
        Supplier<SampleResponseDTO> staticAndDynamicHeader = () -> client.getOneSampleWithStaticAndDynamicHeaders("bar", "boo", "far");
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

    @Test(expectedExceptions = EncodeException.class, expectedExceptionsMessageRegExp = "Invalid GraphQL operation method noMoccaAnnotations, make sure all its parameters are annotated with one Mocca annotation")
    public void noMoccaAnnotationsTest() {
        InvalidClient invalidClient = MoccaClient.Builder.sync("localhost").build(InvalidClient.class);
        invalidClient.noMoccaAnnotations("boo", "far");
    }

    @Test(expectedExceptions = EncodeException.class, expectedExceptionsMessageRegExp = "Invalid GraphQL operation method moreThanOneMoccaAnnotation, make sure all its parameters are annotated with one Mocca annotation")
    public void moreThanOneMoccaAnnotationTest() {
        InvalidClient invalidClient = MoccaClient.Builder.sync("localhost").build(InvalidClient.class);
        invalidClient.moreThanOneMoccaAnnotation("boo", "far");
    }

    private interface InvalidClient extends MoccaClient {
        @Query void noMoccaAnnotations(String par1, String par2);
        @Query void moreThanOneMoccaAnnotation(@Var("par1") String par1, @Var("pat1") @RequestHeaderParam("headername") String par2);
    }

}
