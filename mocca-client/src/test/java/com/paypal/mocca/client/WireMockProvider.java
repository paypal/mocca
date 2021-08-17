package com.paypal.mocca.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * Helper class to configure a WireMock server to support GraphQL client tests
 *
 * @author fabiocarvalho777@gmail.com
 */
class WireMockProvider {

    private static WireMockServer wireMockServer = null;

    // Total number of classes expected to use this provider
    private static final int EXPECTED_NUMBER_OF_TEST_CLASSES = 2;

    // Number of classes using this provider
    private static int classesCounter = 0;

    // Number of classes still expected to use this provider
    private static int remainingClassesCounter = EXPECTED_NUMBER_OF_TEST_CLASSES;

    private static final Map<String, String> DEFAULT_HEADERS = new HashMap<String, String>() {{
        put("Content-Type", "application/json");
        put("Accept", "application/json");
        put("classheader", "classvalue");
    }};

    private WireMockProvider() {
    }

    static synchronized String startServer() throws IOException {
        if (classesCounter == EXPECTED_NUMBER_OF_TEST_CLASSES) {
            throw new IllegalStateException("Total number of test classes already reached! Make sure EXPECTED_NUMBER_OF_TEST_CLASSES is set properly.");
        }
        classesCounter++;
        if (wireMockServer == null) {
            setWireMockServer();
        }
        if (!wireMockServer.isRunning()) {
            wireMockServer.start();
        }

        return wireMockServer.baseUrl();
    }

    static synchronized void stopServer() {
        remainingClassesCounter--;
        if (remainingClassesCounter == 0) {
            wireMockServer.shutdown();
        }
    }

    private static void setWireMockServer() throws IOException {
        WireMockConfiguration configuration = options()
                .dynamicPort()
                //mark verbose flag to true to log request and response
                .notifier(new Slf4jNotifier(false));
        wireMockServer = new WireMockServer(configuration);

        configureQueryStubs();
        configureMutationStubs();
        configureQueryWithHeaderStubs();
    }

    private static void configureQueryStubs() {
        final String EXPECTED_GOOD_REQUEST = "{\"query\":\"query{getOneSample(foo: \\\"boo\\\", bar: \\\"far\\\") {bar foo}}\"}";
        final String GOOD_RESULT = "{\"data\": {\"getOneSample\": {\"foo\": \"boo\",\"bar\": \"far\"}}}";

        final String EXPECTED_NULL_VAR_REQUEST = "{\"query\":\"query{getOneSample(bar: \\\"far\\\") {bar foo}}\"}";
        final String NULL_VAR_RESULT = "{\"data\": {\"getOneSample\": {\"bar\": \"far\"}}}";

        final String EXPECTED_DTO_REQUEST = "{\"query\":\"query{getOneSample(sampleRequest: {bar: \\\"zaz\\\", foo: \\\"boom\\\"}) {bar foo}}\"}";
        final String DTO_RESULT = "{\"data\": {\"getOneSample\": {\"foo\": \"boo\",\"bar\": \"far\"}}}";

        final String EXPECTED_CUSTOM_SELECT_REQUEST = "{\"query\":\"query{getOneSampleCustomSelectionSet(foo: \\\"boo\\\", bar: \\\"far\\\") {foo}}\"}";
        final String CUSTOM_SELECT_RESULT = "{\"data\": {\"getOneSampleCustomSelectionSet\": {\"foo\": \"boo\"}}}";

        final String EXPECTED_IGNORE_FOO_REQUEST = "{\"query\":\"query{getOneSampleWithIgnore(sampleRequest: {bar: \\\"far\\\"}) {bar foo}}\"}";
        final String IGNORE_FOO_RESULT = "{\"data\": {\"getOneSampleWithIgnore\": {\"foo\": null, \"bar\": \"far\"}}}";

        final String EXPECTED_NO_DATA_REQUEST = "{\"query\":\"query{getOneSample(foo: \\\"moo\\\", bar: \\\"czar\\\") {bar foo}}\"}";
        final String NO_DATA_RESULT = "{\"data\": {\"getOneSample\": null}}";

        final String EXPECTED_NO_PARAM_REQUEST = "{\"query\":\"query{getOneSample {bar foo}}\"}";

        final String EXPECTED_ERROR_REQUEST = "{\"query\":\"query{getOneSample(foo: \\\"zoo\\\", bar: \\\"car\\\") {bar foo}}\"}";
        final String ERROR_RESULT = "{\"errors\": [{\"message\": \"Internal Server Error(s) while executing query\"}],\"data\": {\"getOneSample\": null}}";

        final String EXPECTED_GOOD_LIST_REQUEST = "{\"query\":\"query{getSamplesList(foo: \\\"boo\\\", bar: \\\"far\\\") {bar foo}}\"}";
        final String GOOD_LIST_RESULT = "{\"data\": {\"getSamplesList\": [{\"foo\": \"boo1\",\"bar\": \"far1\"}, {\"foo\": \"boo2\",\"bar\": \"far2\"}]}}";

        final String EXPECTED_NO_DATA_LIST_REQUEST = "{\"query\":\"query{getSamplesList(foo: \\\"moo\\\", bar: \\\"czar\\\") {bar foo}}\"}";
        final String NO_DATA_LIST_RESULT = "{\"data\": {\"getSamplesList\": []}}";

        final String EXPECTED_ERROR_LIST_REQUEST = "{\"query\":\"query{getSamplesList(foo: \\\"zoo\\\", bar: \\\"car\\\") {bar foo}}\"}";
        final String ERROR_LIST_RESULT = "{\"errors\": [{\"message\": \"Internal Server Error(s) while executing query\"}],\"data\": {\"getSamplesList\": null}}";

        addGraphQlStub(EXPECTED_GOOD_REQUEST, GOOD_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_NULL_VAR_REQUEST, NULL_VAR_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_DTO_REQUEST, DTO_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_CUSTOM_SELECT_REQUEST, CUSTOM_SELECT_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_IGNORE_FOO_REQUEST, IGNORE_FOO_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_NO_DATA_REQUEST, NO_DATA_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_GOOD_LIST_REQUEST, GOOD_LIST_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_NO_DATA_LIST_REQUEST, NO_DATA_LIST_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_NO_PARAM_REQUEST, GOOD_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_NO_PARAM_REQUEST, NULL_VAR_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_ERROR_REQUEST, ERROR_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_ERROR_LIST_REQUEST, ERROR_LIST_RESULT, DEFAULT_HEADERS);
    }

    private static void configureMutationStubs() {
        final String EXPECTED_GOOD_REQUEST = "{\"query\":\"mutation{addSample(foo: \\\"boo\\\", bar: \\\"far\\\") {bar foo}}\"}";
        final String GOOD_RESULT = "{\"data\": {\"addSample\": {\"foo\": \"boo\",\"bar\": \"far\"}}}";

        final String EXPECTED_DTO_REQUEST = "{\"query\":\"mutation{addSample(sampleRequest: {bar: \\\"czar 100%\\\", foo: \\\"moo\\\"}) {}}\"}";

        final String EXPECTED_NO_DATA_REQUEST = "{\"query\":\"mutation{addSample(foo: \\\"moo\\\", bar: \\\"czar 100%\\\") {bar foo}}\"}";
        final String EXPECTED_NO_DATA_NO_SELECTION_SET_REQUEST = "{\"query\":\"mutation{addSample(bar: \\\"czar 100%\\\", foo: \\\"moo\\\") {}}\"}";
        final String NO_DATA_RESULT = "{\"data\": {\"addSample\": null}}";

        final String EXPECTED_ERROR_REQUEST = "{\"query\":\"mutation{addSample(foo: \\\"zoo\\\", bar: \\\"car\\\") {bar foo}}\"}";
        final String ERROR_RESULT = "{\"errors\": [{\"message\": \"Internal Server Error(s) while executing query\"}],\"data\": {\"addSample\": null}}";

        final String EXPECTED_GOOD_LIST_REQUEST = "{\"query\":\"mutation{addSampleReturnList(foo: \\\"boo\\\", bar: \\\"far\\\") {bar foo}}\"}";
        final String GOOD_LIST_RESULT = "{\"data\": {\"addSampleReturnList\": [{\"foo\": \"boo1\",\"bar\": \"far1\"}, {\"foo\": \"boo2\",\"bar\": \"far2\"}]}}";

        final String EXPECTED_NO_DATA_LIST_REQUEST = "{\"query\":\"mutation{addSampleReturnList(foo: \\\"moo\\\", bar: \\\"czar\\\") {bar foo}}\"}";
        final String NO_DATA_LIST_RESULT = "{\"data\": {\"addSampleReturnList\": []}}";

        final String EXPECTED_ERROR_LIST_REQUEST = "{\"query\":\"mutation{addSampleReturnList(foo: \\\"zoo\\\", bar: \\\"car\\\") { foo bar } }\"}";
        final String ERROR_LIST_RESULT = "{\"errors\": [{\"message\": \"Internal Server Error(s) while executing query\"}],\"data\": {\"addSampleReturnList\": null}}";

        final String EXPECTED_NULL_PARAMETER_REQUEST = "{\"query\":\"mutation{addSample(foo: \\\"zoo\\\", bar: \\\"\\\") {bar foo}}\"}";
        final String NULL_PARAMETER_RESULT = "{\"data\": {\"addSample\": {\"foo\": \"zoo\",\"bar\": \"\"}}}";

        addGraphQlStub(EXPECTED_GOOD_REQUEST, GOOD_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_DTO_REQUEST, NO_DATA_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_NO_DATA_REQUEST, NO_DATA_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_NO_DATA_NO_SELECTION_SET_REQUEST, NO_DATA_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_GOOD_LIST_REQUEST, GOOD_LIST_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_NO_DATA_LIST_REQUEST, NO_DATA_LIST_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_NULL_PARAMETER_REQUEST, NULL_PARAMETER_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_ERROR_REQUEST, ERROR_RESULT, DEFAULT_HEADERS);
        addGraphQlStub(EXPECTED_ERROR_LIST_REQUEST, ERROR_LIST_RESULT, DEFAULT_HEADERS);
    }

    private static void configureQueryWithHeaderStubs() {
        addSingleHeaderStub();
        addSingleHeaderMultipleValueStub();
        addClassHeaderOverrideStub();
        addMultipleHeadersStub();
        addDynamicHeadersStub();
        addHeaderAndDTOStub();
        addMultipleDynamicHeadersStub();
        addStaticAndDynamicHeadersStub();
    }

    private static void addSingleHeaderStub() {
        Map<String, String> singleHeaderMap = new HashMap<>(DEFAULT_HEADERS);
        singleHeaderMap.put("single", "header");
        addHeaderGraphQLStub("getOneSampleWithSingleHeader", singleHeaderMap);
    }

    private static void addSingleHeaderMultipleValueStub() {
        Map<String, String> singleHeaderMap = new HashMap<>(DEFAULT_HEADERS);
        singleHeaderMap.put("sampleheader", "samplevalue, anothervalue, newvalue");
        addHeaderGraphQLStub("getOneSampleWithSingleHeaderMultipleValues", singleHeaderMap);
    }

    private static void addClassHeaderOverrideStub() {
        Map<String, String> overrideHeader = new HashMap<>(DEFAULT_HEADERS);
        overrideHeader.put("classheader", "anothervalue");
        addHeaderGraphQLStub("getOneSampleWithOverrideClassHeader", overrideHeader);
    }

    private static void addMultipleHeadersStub() {
        Map<String, String> singleHeaderMap = new HashMap<>(DEFAULT_HEADERS);
        singleHeaderMap.put("sampleheader", "samplevalue");
        singleHeaderMap.put("anotherheader", "anothervalue");
        addHeaderGraphQLStub("getOneSampleWithMultipleHeaders", singleHeaderMap);
    }

    private static void addDynamicHeadersStub() {
        Map<String, String> headerAndDTOMap = new HashMap<>(DEFAULT_HEADERS);
        headerAndDTOMap.put("sampleheader", "testvalue");
        addHeaderGraphQLDtoStub("getOneSampleWithHeaderAndDTO", headerAndDTOMap);
    }

    private static void addHeaderAndDTOStub() {
        Map<String, String> singleHeaderMap = new HashMap<>(DEFAULT_HEADERS);
        singleHeaderMap.put("dynamicheader", "testvalue");
        addHeaderGraphQLStub("getOneSampleWithDynamicHeader", singleHeaderMap);
    }

    private static void addHeaderGraphQLDtoStub(String operationName, Map<String, String> headersMap) {
        final String SINGLE_HEADER_EXPECTED_GOOD_REQUEST = "{\"query\":\"query{" + operationName + "(sampleRequest: {bar: \\\"far\\\", foo: \\\"boo\\\"}) {bar foo}}\"}";
        final String SINGLE_HEADER_GOOD_RESULT = "{\"data\": {\"" + operationName + "\": {\"foo\": \"boo\",\"bar\": \"far\"}}}";
        addGraphQlStub(SINGLE_HEADER_EXPECTED_GOOD_REQUEST, SINGLE_HEADER_GOOD_RESULT, headersMap);
    }

    private static void addHeaderGraphQLStub(String operationName, Map<String, String> headersMap) {
        final String SINGLE_HEADER_EXPECTED_GOOD_REQUEST = "{\"query\":\"query{" + operationName + "(foo: \\\"boo\\\", bar: \\\"far\\\") {bar foo}}\"}";
        final String SINGLE_HEADER_GOOD_RESULT = "{\"data\": {\"" + operationName + "\": {\"foo\": \"boo\",\"bar\": \"far\"}}}";
        addGraphQlStub(SINGLE_HEADER_EXPECTED_GOOD_REQUEST, SINGLE_HEADER_GOOD_RESULT, headersMap);
    }

    private static void addMultipleDynamicHeadersStub() {
        Map<String, String> headerAndDTOMap = new HashMap<>(DEFAULT_HEADERS);
        headerAndDTOMap.put("header1", "testvalue1");
        headerAndDTOMap.put("header2", "testvalue2");
        addHeaderGraphQLStub("getOneSampleWithMultipleDynamicHeaders", headerAndDTOMap);
    }

    private static void addStaticAndDynamicHeadersStub() {
        Map<String, String> headerAndDTOMap = new HashMap<>(DEFAULT_HEADERS);
        headerAndDTOMap.put("header1", "foo");
        headerAndDTOMap.put("header2", "bar");
        addHeaderGraphQLStub("getOneSampleWithStaticAndDynamicHeaders", headerAndDTOMap);
    }

    private static void addGraphQlStub(final String requestBody, final String responseBody, Map<String, String> headers) {
        MappingBuilder mappingBuilder = post(urlEqualTo("/graphql"));
        headers.forEach((key, value) -> mappingBuilder.withHeader(key, matching(value)));
        wireMockServer.stubFor(mappingBuilder
                .withRequestBody(equalToJson(requestBody))
                .willReturn(aResponse().withHeader("Content-Type", "application/json;charset=UTF-8").withBody(responseBody)));
    }
}
