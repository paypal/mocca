package com.paypal.mocca.client;

import feign.micrometer.MicrometerCapability;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;

/**
 * Mocca supports Micrometer-based metrics, which primarily revolve around HTTP interactions with the target GraphQL server.
 * Notice Mocca metrics are identified with {@code mocca.} prefix.
 * <br>
 * The example below shows how to enable metric gathering in Mocca using Micrometer:
 * <pre><code>
 * import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
 *
 * ...
 *
 * SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
 *
 * BooksAppClient micrometerEnabledClient = MoccaClient.Builder
 *     .sync("localhost:8080/booksapp")
 *     .addCapability(new MoccaMicrometerCapability(meterRegistry))
 *     .build(BooksAppClient.class);
 * </code></pre>
 *
 * @author crankydillo@gmail.com
 */
public final class MoccaMicrometerCapability extends MoccaCapability {

    /**
     * Creates a new {@link MoccaMicrometerCapability}
     *
     * @param meterRegistry the meter registry to be registered
     */
    public MoccaMicrometerCapability(final MeterRegistry meterRegistry) {
        super(new MicrometerCapability(moccafy(meterRegistry)));
    }

    /**
     * This mutates the argument!
     */
    private static MeterRegistry moccafy(final MeterRegistry meterRegistry) {
        meterRegistry.config().meterFilter(new MeterFilter() {
            @Override
            public Meter.Id map(Meter.Id id) {
                return id.withName(id.getName().replaceAll("feign", "mocca"));
            }
        });
        return meterRegistry;
    }
}
