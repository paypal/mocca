package com.paypal.mocca.client;

/**
 * {@link MoccaClient} supports various capabilities (e.g. metrics collection).
 * They are represented by extensions of this class and are most often found in
 * optional libraries.  You register a capability through the 'addCapability'
 * method that you get from MoccaClient's builders.
 *
 * @author crankydillo@gmail.com
 */
abstract class MoccaCapability {
    private final feign.Capability feignCapability;

    protected MoccaCapability(feign.Capability feignCapability) {
        this.feignCapability =
            Arguments.requireNonNull(feignCapability, "Feign capability cannot be null");
    }

    feign.Capability getFeignCapability() {
        return feignCapability;
    }
}