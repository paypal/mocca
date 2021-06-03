package com.paypal.mocca.client.sample;

import com.paypal.mocca.client.MoccaClient;
import com.paypal.mocca.client.annotation.Mutation;
import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.RequestHeader;
import com.paypal.mocca.client.annotation.RequestHeaderParam;
import com.paypal.mocca.client.annotation.SelectionSet;
import com.paypal.mocca.client.annotation.Variable;

import java.util.List;

@RequestHeader("classheader: classvalue")
public interface SampleClient extends MoccaClient {

    // FIXME This method is not used anywhere at the moment because Feign does not call the registered encoder
    //  if the request method does not have any parameter
    @Query
    SampleResponseDTO getOneSample();

    @Query
    SampleResponseDTO getOneSample(String variables);

    @Query
    List<SampleResponseDTO> getSamplesList(String variables);

    @Query
    SampleResponseDTO getOneSample(SampleRequestDTO sampleRequestDTO);

    @Query
    SampleResponseDTO getOneSampleWithIgnore(@Variable(ignore = "foo") SampleRequestDTO sampleRequestDTO);

    @Query
    @RequestHeader("sampleheader : { dynamicvalue }")
    SampleResponseDTO getOneSampleWithHeaderAndDTO(@RequestHeaderParam("dynamicvalue") String headerValue, SampleRequestDTO sampleRequestDTO);

    @Query
    @RequestHeader("single : header")
    SampleResponseDTO getOneSampleWithSingleHeader(String variables);

    @Query
    @RequestHeader("classheader : anothervalue")
    SampleResponseDTO getOneSampleWithOverrideClassHeader(String variables);

    @Query
    @RequestHeader({"sampleheader: samplevalue", "sampleheader:anothervalue", "sampleheader:newvalue"})
    SampleResponseDTO getOneSampleWithSingleHeaderMultipleValues(String variables);

    @Query
    @RequestHeader({"sampleheader : samplevalue", "anotherheader: anothervalue"})
    SampleResponseDTO getOneSampleWithMultipleHeaders(String variables);

    @Query
    @RequestHeader({"header1 : { headerValue1 }", "header2 : { headerValue2 }"})
    SampleResponseDTO getOneSampleWithMultipleDynamicHeaders(
            @RequestHeaderParam("headerValue1") String headerValue1,
            @RequestHeaderParam("headerValue2") String headerValue2,
            String variables);

    @Query
    @RequestHeader({"header1 : foo", "header2 : { headerValue2 }"})
    SampleResponseDTO getOneSampleWithStaticAndDynamicHeaders(
            @RequestHeaderParam("headerValue2") String headerValue2,
            String variables);

    @Query
    @RequestHeader("dynamicheader : { headerValue }")
    SampleResponseDTO getOneSampleWithDynamicHeader(@RequestHeaderParam("headerValue") String headerValue, String variables);

    @Query
    @SelectionSet("{foo}")
    SampleResponseDTO getOneSampleCustomSelectionSet(String variables);

    @Mutation
    SampleResponseDTO addSample(String variables);

    @Mutation
    void addSample(SampleRequestDTO sampleRequestDTO);

    @Mutation
    List<SampleResponseDTO> addSampleReturnList(String variables);

}
