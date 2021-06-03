package com.paypal.mocca.client;

import feign.Client;

public class PoorFeignCapability implements feign.Capability {
    static final String errorMessage = "I have nothing to give:(";
    @Override
    public Client enrich(Client client) {
        throw new RuntimeException(errorMessage);
    }
}
