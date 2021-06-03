package com.paypal.mocca.client.sample;


import com.paypal.mocca.client.MoccaClient;
import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.RequestHeader;

@RequestHeader("classheader: { classvalue }")
public interface DynamicHeaderClient extends MoccaClient {

    @Query
    SampleResponseDTO getOneSample(String variables);
}
