package com.paypal.mocca.client;

import com.paypal.mocca.client.sample.DynamicHeaderClient;
import org.testng.annotations.Test;

/**
 * Tests to verify dynamic header cannot be added at class level
 */
public class MoccaDynamicHeaderTest {

    @Test(expectedExceptions = MoccaException.class, expectedExceptionsMessageRegExp = "(Header value:\\{ classvalue } at class level cannot be dynamic)")
    public void verifyDynamicHeader() {
        DynamicHeaderClient client =
            MoccaClient.Builder.sync("dummyurl")
                .defaultClient()
                .build(DynamicHeaderClient.class);
        String queryVariables = "foo: \"zoo\", bar: \"car\"";
        client.getOneSample(queryVariables);
    }
}
