package com.paypal.mocca.client.sample;

import com.paypal.mocca.client.MoccaClient;
import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.RequestHeader;
import com.paypal.mocca.client.annotation.Var;

import java.util.concurrent.CompletableFuture;

@RequestHeader("classheader: classvalue")
public interface AsyncSampleClient extends MoccaClient {

    @Query
    CompletableFuture<SampleResponseDTO> getOneSample(@Var("foo") String foo, @Var("bar") String bar);

}
