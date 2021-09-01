package com.paypal.mocca.client;

import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.RequestHeaderParam;
import com.paypal.mocca.client.annotation.Var;
import com.paypal.mocca.client.sample.AsyncSampleClient;
import com.paypal.mocca.client.sample.SampleClient;
import com.paypal.mocca.client.sample.SampleRequestDTO;
import com.paypal.mocca.client.sample.SampleResponseDTO;
import com.paypal.mocca.client.sample.SuperComplexResponseType;
import com.paypal.mocca.client.sample.SuperComplexResponseType.SuperComplexResponseField;
import com.paypal.mocca.client.sample.SuperComplexSampleType;
import com.paypal.mocca.client.sample.SuperComplexSampleType.SuperComplexField;
import feign.codec.DecodeException;
import feign.codec.EncodeException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.FileAssert.fail;

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
    public void queryNullVariableTest() {
        SampleResponseDTO result = client.getOneSample(null, "far");
        assertNotNull(result);
        assertEquals(result.getFoo(), null);
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

    @Test
    public void queryOffsetDateTimeTest() {
        OffsetDateTime expectedDateTime = OffsetDateTime.parse("2021-08-17T18:12:22.470076-03:00");
        OffsetDateTime actualDateTime = client.getDateTime(expectedDateTime);
        assertNotNull(actualDateTime);
        assertEquals(actualDateTime.toInstant(), expectedDateTime.toInstant());
    }

    @Test
    public void queryComplexDataTest() {
        List<String> stringList = Arrays.asList("blue", "yellow", "guacamole");
        SuperComplexField innerComplexField = new SuperComplexField(1, "one", false, stringList, null, null);
        SuperComplexField complexField = new SuperComplexField(1, "one", false, stringList, innerComplexField, Collections.singletonList(innerComplexField));
        List<SuperComplexField> complexList = Collections.singletonList(complexField);
        OffsetDateTime dateTime = OffsetDateTime.parse("2021-08-17T18:12:22.470076-03:00");

        SuperComplexSampleType superComplexSampleType = new SuperComplexSampleType(7, "seven", true, complexField, complexList, stringList);
        superComplexSampleType.setDateTime(dateTime);

        SuperComplexResponseType superComplexResponse = client.getSuperComplexStuff(superComplexSampleType);

        assertNotNull(superComplexResponse);
        assertEquals(superComplexResponse.getIntVar(), 7);
        assertEquals(superComplexResponse.getStringVar(), "seven");
        assertTrue(superComplexResponse.isBooleanVar());
        assertEquals(superComplexResponse.getStringListVar(), stringList);
        assertEquals(superComplexResponse.getDateTime().toInstant(), dateTime.toInstant());

        SuperComplexResponseField expectedComplexField = new SuperComplexResponseField()
                .setInnerBooleanVar(complexField.isInnerBooleanVar())
                .setInnerIntVar(complexField.getInnerIntVar())
                .setInnerStringVar(complexField.getInnerStringVar())
                .setInnerStringListVar(complexField.getInnerStringListVar());

        assertEquals(superComplexResponse.getComplexField(), expectedComplexField);
        assertEquals(superComplexResponse.getComplexListVar(), Collections.singletonList(expectedComplexField));
    }

    @Test
    public void queryOptionalTest() {

        // Testing optional explicitly used in the return type and request variable
        SampleRequestDTO sampleRequestDTO = new SampleRequestDTO("boom", "zaz");
        Optional<SampleResponseDTO> result = client.getOneSample(Optional.of(sampleRequestDTO));
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(result.get().getFoo(), "boo");
        assertEquals(result.get().getBar(), "far");

        // Testing optional used inside the return type and the request variable POJO
        SuperComplexSampleType superComplexSampleType = new SuperComplexSampleType(1, "one", true, null, null, null);
        superComplexSampleType.setOptionalField("love");
        SuperComplexResponseType superComplexResponse = client.getSuperComplexStuff(superComplexSampleType);

        assertNotNull(superComplexResponse);
        assertEquals(superComplexResponse.getIntVar(), 1);
        assertEquals(superComplexResponse.getStringVar(), "one");
        assertTrue(superComplexResponse.isBooleanVar());
        assertNull(superComplexResponse.getComplexField());
        assertNull(superComplexResponse.getComplexListVar());
        assertNull(superComplexResponse.getStringListVar());
        assertNull(superComplexResponse.getDateTime());
        assertTrue(superComplexResponse.getOptionalField().isPresent());
        assertEquals(superComplexResponse.getOptionalField().get(), "love");
    }

    @Test(expectedExceptions = DecodeException.class, expectedExceptionsMessageRegExp = "(Internal Server Error\\(s\\) while executing query)")
    public void queryErrorTest() {
        client.getOneSample("zoo", "car");
    }

    @Test
    public void queryListTest() {
        List<SampleResponseDTO> sampleResponseDTOs = client.getSamplesList("boo", "far");
        assertNotNull(sampleResponseDTOs);
        assertEquals(sampleResponseDTOs.size(), 2);
        assertEquals(sampleResponseDTOs.get(0).getFoo(), "boo1");
        assertEquals(sampleResponseDTOs.get(0).getBar(), "far1");
        assertEquals(sampleResponseDTOs.get(1).getFoo(), "boo2");
        assertEquals(sampleResponseDTOs.get(1).getBar(), "far2");
    }

    @Test
    public void queryListParameterTest() {
        List<SampleRequestDTO> sampleRequests = Arrays.asList(
            new SampleRequestDTO("boo1", "far1"), new SampleRequestDTO("boo2", "far2")
        );
        List<Integer> numbers = Arrays.asList(1, 2, 3);

        List<SampleResponseDTO> sampleResponseDTOs = client.getSamplesList(sampleRequests, numbers, "seven", 7);
        assertNotNull(sampleResponseDTOs);
        assertEquals(sampleResponseDTOs.size(), 2);
        assertEquals(sampleResponseDTOs.get(0).getFoo(), "boo1");
        assertEquals(sampleResponseDTOs.get(0).getBar(), "far1");
        assertEquals(sampleResponseDTOs.get(1).getFoo(), "boo2");
        assertEquals(sampleResponseDTOs.get(1).getBar(), "far2");
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
        final AsyncSampleClient asyncClient = MoccaClient.Builder.async(serverBaseUrl).build(AsyncSampleClient.class);
        final SampleResponseDTO result = asyncClient.getOneSample("boo", "far").get(5, TimeUnit.SECONDS);
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

    @Test
    public void selectionSetCycleTest() {
        try {
            client.getResponseWithCycle();
            fail("A Mocca exception was supposed to have been thrown!");
        } catch (EncodeException e) {
            assertEquals(e.getCause().getCause().getCause().getMessage(),
                    "Selection set cannot be specified as there is a cycle in the return type caused by class com.paypal.mocca.client.sample.CyclePojo");
        }
    }

    private interface InvalidClient extends MoccaClient {
        @Query void noMoccaAnnotations(String par1, String par2);
        @Query void moreThanOneMoccaAnnotation(@Var("par1") String par1, @Var("pat1") @RequestHeaderParam("headername") String par2);
    }

}
