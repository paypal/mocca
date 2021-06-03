package com.paypal.mocca.client.sample;

import com.paypal.mocca.client.MoccaClient;
import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.RequestHeader;

import java.util.concurrent.CompletableFuture;

@RequestHeader("classheader: classvalue")
public interface AsyncSampleClient extends MoccaClient {

    @Query
    CompletableFuture<SampleResponseDTO> getOneSample(String variables);

}
