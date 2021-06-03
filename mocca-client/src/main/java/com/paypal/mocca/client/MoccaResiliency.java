package com.paypal.mocca.client;

import feign.Feign;

/**
 * This is a mechanism for supporting {@link MoccaClient} resiliency features.  Similar to
 * {@link MoccaHttpClient}, the actual functionality is delivered via an opt-in additional library,
 * which is then registered by using {@link MoccaClient.Builder.SyncBuilder#resiliency(MoccaResiliency)}.
 *
 * @author crankydillo@gmail.com
 */
abstract class MoccaResiliency {
    // FIXME this messiness exists to support resiliency using resilience4j-feign
    //  which operates at the builder level; however, we want to present it as a
    //  in a non-coupled way with via our builder..  Ultimately, we believe feign
    //  will release resilience4j as a feign.Capability and then we can get rid of
    //  this.  An important thing to note is this nasty impl stuff is hidden from
    //  the user.  The main user problem is that if they add multiple of these to
    //  the builder a runtime exception will be generated.
    //
    //  This can be done as a `MoccaCapability`, but it will make the impl look messier
    //  and could have some effects like runtime exceptions if more than one builder-based
    //  capability is added.  I had most of that coded and then went this route..
    private final Feign.Builder feignBuilder;

    protected MoccaResiliency(Feign.Builder feignBuilder) {
        this.feignBuilder =
            Arguments.requireNonNull(feignBuilder, "Feign builder cannot be null");
    }

    Feign.Builder getFeignBuilder() {
        return feignBuilder;
    }
}
