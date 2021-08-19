package com.paypal.mocca.client.sample;

import com.paypal.mocca.client.MoccaClient;
import com.paypal.mocca.client.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RequestHeader("classheader: classvalue")
public interface SampleClient extends MoccaClient {

    @Query
    SampleResponseDTO getOneSample();

    @Query
    SampleResponseDTO getOneSample(@Var(raw = true) String variables);

    @Query
    SampleResponseDTO getOneSample(@Var("foo") String foo, @Var("bar") String bar);

    @Query
    List<SampleResponseDTO> getSamplesList(@Var("foo") String foo, @Var("bar") String bar);

    @Query
    SampleResponseDTO getOneSample(@Var("sampleRequest") SampleRequestDTO sampleRequestDTO);

    @Query
    SampleResponseDTO getOneSampleWithIgnore(@Var(value = "sampleRequest", ignore = "foo") SampleRequestDTO sampleRequestDTO);

    @Query
    @RequestHeader("sampleheader : { dynamicvalue }")
    SampleResponseDTO getOneSampleWithHeaderAndDTO(@RequestHeaderParam("dynamicvalue") String headerValue, @Var("sampleRequest") SampleRequestDTO sampleRequestDTO);

    @Query
    @RequestHeader("single : header")
    SampleResponseDTO getOneSampleWithSingleHeader(@Var("foo") String foo, @Var("bar") String bar);

    @Query
    @RequestHeader("classheader : anothervalue")
    SampleResponseDTO getOneSampleWithOverrideClassHeader(@Var("foo") String foo, @Var("bar") String bar);

    @Query
    @RequestHeader({"sampleheader: samplevalue", "sampleheader:anothervalue", "sampleheader:newvalue"})
    SampleResponseDTO getOneSampleWithSingleHeaderMultipleValues(@Var("foo") String foo, @Var("bar") String bar);

    @Query
    @RequestHeader({"sampleheader : samplevalue", "anotherheader: anothervalue"})
    SampleResponseDTO getOneSampleWithMultipleHeaders(@Var("foo") String foo, @Var("bar") String bar);

    @Query
    @RequestHeader({"header1 : { headerValue1 }", "header2 : { headerValue2 }"})
    SampleResponseDTO getOneSampleWithMultipleDynamicHeaders(
            @RequestHeaderParam("headerValue1") String headerValue1,
            @RequestHeaderParam("headerValue2") String headerValue2,
            @Var("foo") String foo, @Var("bar") String bar);

    @Query
    @RequestHeader({"header1 : foo", "header2 : { headerValue2 }"})
    SampleResponseDTO getOneSampleWithStaticAndDynamicHeaders(
            @RequestHeaderParam("headerValue2") String headerValue2,
            @Var("foo") String foo, @Var("bar") String bar);

    @Query
    @RequestHeader("dynamicheader : { headerValue }")
    SampleResponseDTO getOneSampleWithDynamicHeader(@RequestHeaderParam("headerValue") String headerValue, @Var("foo") String foo, @Var("bar") String bar);

    @Query
    @SelectionSet("{foo}")
    SampleResponseDTO getOneSampleCustomSelectionSet(@Var("foo") String foo, @Var("bar") String bar);

    @Mutation
    SampleResponseDTO addSample(@Var("foo") String foo, @Var("bar") String bar);

    @Mutation
    void addSample(@Var("sampleRequest") SampleRequestDTO sampleRequestDTO);

    @Mutation
    List<SampleResponseDTO> addSampleReturnList(@Var("foo") String foo, @Var("bar") String bar);

    @Query
    OffsetDateTime getDateTime(@Var("dateTimeToReturn") OffsetDateTime dateTimeToReturn);

    @Query
    SuperComplexResponseType getSuperComplexStuff(@Var("superComplexSampleType") SuperComplexSampleType superComplexSampleType);

}