package com.paypal.mocca.client;

import feign.AsyncClient;
import feign.AsyncFeign;
import feign.Client;
import feign.Feign;
import feign.Request;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Applications are supposed to create an interface, extending this one, to define their GraphQL client API. Each
 * GraphQL operation can be defined using Mocca annotations under {@link com.paypal.mocca.client.annotation}.
 * <br>
 * <br>
 * The most basic rules when writing a client API can be see below:
 * <br>
 * <ol>
 *     <li>The Java interface must extend {@link MoccaClient}</li>
 *     <li>The signature of each GraphQL operation method must:
 *         <ol>
 *             <li>Declare a list of parameters containing exactly one parameter, analog to the Variables section declared in the GraphQL schema of same operation. This parameter name is not relevant, but its type must be one of the following:
 *                 <ol>
 *                     <li>A String where the application can specify a raw representation of each variable and its value (example: {@code "id: 107, name: \"Murphy\", breed: \"Maine Coon\", color: \"gray\""}).</li>
 *                     <li>A DTO</li>
 *                 </ol>
 *             </li>
 *             <li>Declare a return type (it cannot be {@code void}) analog to the type declared in the GraphQL schema of same operation. This return type must be one of the following:
 *                 <ol>
 *                     <li>A primitive</li>
 *                     <li>A data transfer object (DTO)</li>
 *                     <li>A {@link java.util.List} containing a DTO</li>
 *                 </ol>
 *             </li>
 *         </ol>
 *     </li>
 *     <li>Each GraphQL operation method must be annotated with {@link com.paypal.mocca.client.annotation.Query} or {@link com.paypal.mocca.client.annotation.Mutation} annotation depending whether the operation is a GraphQL query or mutation respectively.</li>
 *     <li>The method name should be the same as the GraphQL operation name. If a different name is desired for the Java method, then the GraphQL operation name must be set using the {@code name} attribute in {@link com.paypal.mocca.client.annotation.Query} or {@link com.paypal.mocca.client.annotation.Mutation}.</li>
 * </ol>
 * <br>
 * You can see below an example of a simple client API.
 * <pre><code>
 * import com.paypal.mocca.client.MoccaClient;
 * import com.paypal.mocca.client.annotation.Mutation;
 * import com.paypal.mocca.client.annotation.Query;
 * import com.paypal.mocca.client.annotation.SelectionSet;
 * import com.paypal.mocca.client.annotation.Var;
 *
 * public interface BooksAppClient extends MoccaClient {
 *
 *     &#064;Query
 *     &#064;SelectionSet("{id, name}")
 *     List&#60;Book&#62; getBooks(&#064;Var("authorId") long authorId);
 *
 *     &#064;Query
 *     Book getBook(&#064;Var("id") long id);
 *
 *     &#064;Mutation
 *     Author addAuthor(&#064;Var(value = "author", ignore = "books") Author author);
 *
 *     &#064;Mutation
 *     Book addBook(&#064;Var("book") Book book);
 *
 * }</code></pre>
 *
 * @author fabiocarvalho777@gmail.com
 */
public interface MoccaClient {

    /**
     * A {@link MoccaClient} builder for Mocca clients.
     * <br>
     * See an example below of how to configure a sync Mocca client using OkHttp as HTTP client.
     * <br>
     * <pre><code>
     * BooksAppClient client = MoccaClient.Builder
     *     .sync("http://localhost:8080/booksapp")
     *     .client(new MoccaOkHttpClient())
     *     .build(BooksAppClient.class);
     * </code></pre>
     */
    class Builder {

        private Builder() {
        }

        /**
         * Provides a builder to create a Mocca sync client. If an HTTP client is not set in the builder, a {@link
         * MoccaDefaultHttpClient} will be used. See {@link Builder.SyncBuilder#client} for further
         * information.
         * <br>
         * See an example below of how to configure a sync Mocca client using OkHttp as HTTP client.
         * <br>
         * <pre><code>
         * BooksAppClient client = MoccaClient.Builder
         *     .sync("http://localhost:8080/booksapp")
         *     .client(new MoccaOkHttpClient())
         *     .build(BooksAppClient.class);
         * </code></pre>
         *
         * @param serverBaseUrl GraphQL server base URL (e.g. https://your.graphql.server/context). Do not end the URI
         *                      path with graphql, that is added automatically by Mocca.
         * @return a {@link Builder.SyncBuilder}.
         */
        public static Builder.SyncBuilder sync(final String serverBaseUrl) {
            return new Builder.SyncBuilder(serverBaseUrl);
        }

        /**
         * Provides a builder to create a Mocca asynchronous client. If an HTTP client is not set in the builder, a
         * {@link MoccaDefaultHttpClient} will be used, executed by a cached thread pool executor service. See {@link
         * Builder.AsyncBuilder#client(MoccaAsyncHttpClient)} and {@link MoccaExecutorHttpClient} for further
         * information.
         * <br>
         * See an example below of how to configure an asynchronous Mocca client using Apache HTTP client 5 as HTTP client.
         * <br>
         * <pre><code>
         * MoccaAsyncApache5Client asyncHttpClient = new MoccaAsyncApache5Client();
         *
         * BooksAppClient asyncClient = MoccaClient.Builder
         *     .async("http://localhost:8080/booksapp")
         *     .client(asyncHttpClient)
         *     .build(BooksAppClient.class);
         * </code></pre>
         *
         * @param serverBaseUrl GraphQL server base URL (e.g. https://your.graphql.server/context). Do not end the URI
         *                      path with graphql, that is added automatically by Mocca.
         * @return a {@link Builder.AsyncBuilder}.
         */
        public static Builder.AsyncBuilder async(final String serverBaseUrl) {
            return new Builder.AsyncBuilder(serverBaseUrl);
        }

        /**
         * A {@link MoccaClient} builder for synchronous clients.
         * <br>
         * See an example below of how to configure a sync Mocca client using OkHttp as HTTP client.
         * <br>
         * <pre><code>
         * BooksAppClient client = MoccaClient.Builder
         *     .sync("http://localhost:8080/booksapp")
         *     .client(new MoccaOkHttpClient())
         *     .build(BooksAppClient.class);
         * </code></pre>
         * Notice this builder is not thread-safe.
         */
        public static class SyncBuilder  {
            private MoccaHttpClient moccaHttpClient;
            private final String serverBaseUrl;

            private SyncBuilder(final String serverBaseUrl) {
                this.serverBaseUrl = Arguments.requireNonNull(serverBaseUrl);
            }

            /**
             * Sets a custom {@link MoccaHttpClient} to be used for GraphQL requests.  The supplied client supports
             * Mocca-specified timeouts.  See {@link MoccaHttpClient.WithRequestTimeouts} for more information.
             * <br>
             * <br>
             * Mocca uses behind the scenes an HTTP client to make the GraphQL calls. By default, JDK {@link
             * java.net.HttpURLConnection} is used as HTTP client, and no additional dependency is required to use it.
             * <br>
             * However, if preferred, a custom HTTP client can be specified by adding an extra Mocca dependency and
             * setting the HTTP client when creating the Mocca client builder (using method client), as seen below:
             * <br>
             * <pre><code>
             * BooksAppClient client = MoccaClient.Builder
             *     .sync("http://localhost:8080/booksapp")
             *     .client(new MoccaOkHttpClient())
             *     .build(BooksAppClient.class);
             * </code></pre>
             * <br>
             * All HTTP clients supported by Mocca are documented in the table below, along with the library to be added
             * as dependency to the application, and the client class to be used in the Mocca builder.
             * <br>
             * <br>
             * <table border="1">
             *     <caption>HTTP clients supported by Mocca</caption>
             *     <tr><th>HTTP client</th><th>Dependency</th><th>Client class</th><th>Notes</th></tr>
             *     <tr><td>OkHttp client</td><td>com.paypal.mocca:mocca-okhttp:0.0.1</td><td>com.paypal.mocca.client.MoccaOkHttpClient</td><td></td></tr>
             *     <tr><td>Apache HTTP client 5</td><td>com.paypal.mocca:mocca-hc5:0.0.1</td><td>com.paypal.mocca.client.MoccaApache5Client</td><td>Apache HTTP client 5</td></tr>
             *     <tr><td>Apache HTTP client</td><td>com.paypal.mocca:mocca-apache:0.0.1</td><td>com.paypal.mocca.client.MoccaApacheClient</td><td>Original Apache HTTP client</td></tr>
             *     <tr><td>Google HTTP client</td><td>com.paypal.mocca:mocca-google:0.0.1</td><td>com.paypal.mocca.client.MoccaGoogleHttpClient</td><td></td></tr>
             *     <tr><td>Java HTTP2 client</td><td>com.paypal.mocca:mocca-http2:0.0.1</td><td>com.paypal.mocca.client.MoccaHttp2Client</td><td>HTTP2 client provided by the JDK</td></tr>
             * </table>
             * <br>
             * <br>
             * The table above includes only synchronous clients, and the code samples documented here are specific to synchronous clients. For information about supported asynchronous clients, and how to configure it, please see {@link AsyncBuilder#client(MoccaAsyncHttpClient)}.
             * <br>
             * <br>
             * All Mocca classes mentioned in the table above work as a wrapper containing a default instance of the HTTP client. If the application needs the HTTP client to be configured in a certain manner, there are two ways to achieve that:
             * <ol>
             *     <li>Mocca client builder could be used to do so, providing a common API regardless of the type of HTTP client used.</li>
             *     <li>A custom instance of the HTTP client could be provided as a constructor parameter to the Mocca HTTP client wrapper.</li>
             * </ol>
             * <br>
             * <pre><code>
             * okhttp3.OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
             *     .build();
             *
             * BooksAppClient client = MoccaClient.Builder
             *     .sync("localhost:8080/booksapp")
             *     .client(new MoccaOkHttpClient(okHttpClient))
             *     .build(BooksAppClient.class);
             * </code></pre>
             *
             * @param moccaHttpClient Mocca HTTP client that supports Mocca-specified timeouts.
             * @return a builder that supports Mocca-based timeouts.
             */
            public Builder.SyncBuilder.WithRequestTimeouts client(final MoccaHttpClient.WithRequestTimeouts moccaHttpClient) {
                this.moccaHttpClient = Arguments.requireNonNull(moccaHttpClient);
                return this.new WithRequestTimeouts();
            }

            /**
             * Similar to {@link #client(MoccaHttpClient.WithRequestTimeouts)}.  However, this does not support
             * Mocca-specified timeouts.  See {@link MoccaHttpClient.WithoutRequestTimeouts} for more information.
             *
             * @param moccaHttpClient Mocca HTTP client that does not support Mocca-specified timeouts.
             * @return a builder that does not support Mocca-based timeouts.
             */
            public Builder.SyncBuilder.WithoutRequestTimeouts client(final MoccaHttpClient.WithoutRequestTimeouts moccaHttpClient) {
                this.moccaHttpClient = Arguments.requireNonNull(moccaHttpClient);
                return this.new WithoutRequestTimeouts();
            }

            /**
             * Use the default HTTP client provided by Mocca.
             *
             * @return a builder that supports Mocca-based timeouts.
             */
            public Builder.SyncBuilder.WithRequestTimeouts defaultClient() {
                return this.new WithRequestTimeouts();
            }

            public class WithRequestTimeouts extends Base {
                public WithRequestTimeouts options(
                    final Duration connectTimeout,
                    final Duration readTimeout,
                    final boolean followRedirects
                ) {
                    this.optionsBuilder.options(connectTimeout, readTimeout, followRedirects);
                    return this;
                }
            }

            // Marker, not technically required..
            public class WithoutRequestTimeouts extends Base {}

            public abstract class Base extends Builder.BaseBuilder<Builder.SyncBuilder.Base> {
                protected final OptionsBuilder optionsBuilder = new OptionsBuilder();
                private MoccaResiliency resiliency;

                Base() {
                    super(serverBaseUrl);
                }


                /**
                 * Adds a {@link MoccaResiliency} feature to be configured in this client builder.
                 *
                 * @param resiliency the resilience object to be set in this builder
                 * @return this builder
                 */
                public Base resiliency(final MoccaResiliency resiliency) {
                    this.resiliency = resiliency;
                    return this;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public <C extends MoccaClient> C build(final Class<C> apiType) {
                    Feign.Builder builder = (resiliency != null) ? resiliency.getFeignBuilder() : Feign.builder();

                    MoccaFeignEncoder encoder = new MoccaFeignEncoder();
                    builder = builder.contract(new MoccaFeignContract())
                        .options(optionsBuilder.options().orElse(new Request.Options()))
                        .encoder(encoder)
                        .decoder(new MoccaFeignDecoder());

                    if (resiliency == null) {
                        builder.invocationHandlerFactory(new MoccaFeignInvocationHandlerFactory());
                    }

                    if (moccaHttpClient != null) {
                        builder = builder.client(moccaHttpClient.getFeignClient());
                    }
                    for (final MoccaCapability c : capabilities) {
                        builder = builder.addCapability(c.getFeignCapability());
                    }
                    C client =  builder.target(apiType, graphQLUrlString);
                    // the client object is needed in the encoder to perform
                    // bean validation for the request
                    encoder.setClient(client);
                    return client;
                }
            }
        }

        /**
         * A {@link MoccaClient} builder for asynchronous clients.
         * <br>
         * See an example below of how to configure an asynchronous Mocca client using Apache HTTP client 5 as HTTP client.
         * <br>
         * <pre><code>
         * MoccaAsyncApache5Client asyncHttpClient = new MoccaAsyncApache5Client();
         *
         * BooksAppClient asyncClient = MoccaClient.Builder
         *     .async("http://localhost:8080/booksapp")
         *     .client(asyncHttpClient)
         *     .build(BooksAppClient.class);
         * </code></pre>
         * Notice this builder is not thread-safe.
         */
        public static class AsyncBuilder {
            private MoccaAsyncHttpClient<?> moccaAsyncHttpClient;
            private String serverBaseUrl;

            public AsyncBuilder(String serverBaseUrl) {
                this.serverBaseUrl = serverBaseUrl;
            }

            /**
             * Sets a custom {@link MoccaAsyncHttpClient} to be used for GraphQL requests. The supplied client supports
             * Mocca-specified timeouts.  See {@link MoccaAsyncHttpClient.WithRequestTimeouts} for more information.
             * <br>
             * <br>
             * Mocca supports two types of asynchrounous development approaches:
             * <ol>
             *     <li>Using an asynchronous HTTP client</li>
             *     <li>Using a sync HTTP client executed by an user provided executor service</li>
             * </ol>
             * <br>
             * Each one of them are described in details below.
             * <br>
             * <br>
             * <p><strong>Using an asynchronous HTTP client</strong></p>
             * The table below shows all asynchronous HTTP clients supported by Mocca, followed by an example of how to configure a Mocca asynchronous client.
             * <br>
             * <br>
             * <table border="1">
             *     <caption>Async HTTP clients supported by Mocca</caption>
             *     <tr><th>HTTP client</th><th>Dependency</th><th>Client class</th></tr>
             *     <tr><td>Apache HTTP client 5</td><td>com.paypal.mocca:mocca-hc5:0.0.1</td><td>com.paypal.mocca.client.MoccaAsyncApache5Client</td></tr>
             * </table>
             * <pre><code>
             * MoccaAsyncApache5Client asyncHttpClient = new MoccaAsyncApache5Client();
             *
             * BooksAppClient asyncClient = MoccaClient.Builder
             *     .async("http://localhost:8080/booksapp")
             *     .client(asyncHttpClient)
             *     .build(BooksAppClient.class);
             * </code></pre>
             * <br>
             * Similar to Mocca synchronous clients, the application can also provide configuration using the HTTP client object directly or using the builder settings, as seen below.
             * <br>
             * <pre><code>
             * CloseableHttpAsyncClient apacheAsyncHttpClient = HttpAsyncClients
             *     .custom()
             *     .disableAuthCaching()
             *     .disableCookieManagement()
             *     .build();
             * apacheAsyncHttpClient.start();
             *
             * MoccaAsyncApache5Client asyncHttpClient = new MoccaAsyncApache5Client(apacheAsyncHttpClient);
             *
             * BooksAppClient asyncClient = MoccaClient.Builder
             *     .async("http://localhost:8080/booksapp")
             *     .client(asyncHttpClient)
             *     .build(BooksAppClient.class);
             * </code></pre>
             * <p><strong>Using a sync HTTP client run by an executor service</strong></p>
             * The example below shows how to configure a Mocca asynchronous client using a regular Mocca sync client, but run by an application-provided executor service.
             * <br>
             * <pre><code>
             * ExecutorService executorService = Executors.newCachedThreadPool();
             * MoccaExecutorHttpClient&#60;OkHttpClient&#62; executorClient = new MoccaExecutorHttpClient&#60;&#62;(new MoccaOkHttpClient(), executorService);
             *
             * AsyncBooksAppClient asyncClient = MoccaClient.Builder
             *     .async("localhost:8080/booksapp")
             *     .client(executorClient)
             *     .build(AsyncBooksAppClient.class);
             * </code></pre>
             * <br>
             * The example below shows an application providing custom configuration using the HTTP client object directly and, for illustration purposes, using the builder settings as well.
             * <br>
             * <pre><code>
             * okhttp3.OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
             *     .build();
             *
             * ExecutorService executorService = Executors.newCachedThreadPool();
             *
             * MoccaExecutorHttpClient&#60;OkHttpClient&#62; executorClient = new MoccaExecutorHttpClient&#60;&#62;(new MoccaOkHttpClient(), executorService);
             *
             * AsyncBooksAppClient asyncClient = MoccaClient.Builder
             *         .async("localhost:8080/booksapp")
             *         .client(executorClient)
             *         .build(AsyncBooksAppClient.class);
             * </code></pre>
             *
             * @param moccaAsyncHttpClient Mocca HTTP asynchronous client that supports Mocca-specified timeouts.
             * @param <CC> An optional context
             * @return a builder that supports Mocca-based timeouts.
             */

            public <CC> Builder.AsyncBuilder.WithRequestTimeouts<CC> client(
                final MoccaAsyncHttpClient.WithRequestTimeouts<CC> moccaAsyncHttpClient
            ) {
                this.moccaAsyncHttpClient = Arguments.requireNonNull(moccaAsyncHttpClient);
                return this.new WithRequestTimeouts<>();
            }

            /**
             * Similar to {@link #client(MoccaAsyncHttpClient.WithRequestTimeouts)}.  However, this does not support
             * Mocca-specified timeouts.  See {@link MoccaAsyncHttpClient.WithoutRequestTimeouts} for more information.
             *
             * @param moccaAsyncHttpClient Mocca HTTP asynchronous client that does not support Mocca-specified timeouts.
             * @param <CC> An optional context
             * @return a builder that does not support Mocca-specified timeouts.
             */
            public <CC> Builder.AsyncBuilder.WithoutRequestTimeouts<CC> client(
                final MoccaAsyncHttpClient.WithoutRequestTimeouts<CC> moccaAsyncHttpClient
            ) {
                this.moccaAsyncHttpClient = Arguments.requireNonNull(moccaAsyncHttpClient);
                return this.new WithoutRequestTimeouts<>();
            }

            /**
             * Use the default HTTP asynchronous client provided by Mocca.
             *
             * @param <CC> An optional context
             * @return a builder that supports Mocca-specified timeouts.
             */
            public <CC> Builder.AsyncBuilder.WithRequestTimeouts<CC> defaultClient() {
                return this.new WithRequestTimeouts<>();
            }

            public Builder.AsyncBuilder client(final MoccaAsyncHttpClient<?> moccaAsyncHttpClient) {
                this.moccaAsyncHttpClient = Arguments.requireNonNull(moccaAsyncHttpClient);
                return this;
            }

            public class WithRequestTimeouts<CC> extends AsyncBuilder.Base<CC, AsyncBuilder.WithRequestTimeouts<CC>> {
                public WithRequestTimeouts<CC> options(
                    final Duration connectTimeout,
                    final Duration readTimeout,
                    final boolean followRedirects
                ) {
                    this.optionsBuilder.options(connectTimeout, readTimeout, followRedirects);
                    return this;
                }
            }

            // Marker, not technically required..
            public class WithoutRequestTimeouts<CC>
                extends AsyncBuilder.Base<CC, AsyncBuilder.WithRequestTimeouts<CC>> {}


            abstract class Base<CC, B extends AsyncBuilder.Base<CC, B>> extends Builder.BaseBuilder<B> {
                protected final OptionsBuilder optionsBuilder = new OptionsBuilder();

                Base() {
                    super(serverBaseUrl);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public <C extends MoccaClient> C build(final Class<C> apiType) {
                    final AsyncClient<?> feignAsyncClient = moccaAsyncHttpClient != null ? moccaAsyncHttpClient.getFeignAsyncClient() : null;
                    return new ClientSpecificBuilder<>(feignAsyncClient).build(apiType);
                }

                private class ClientSpecificBuilder<CC> {
                    private final AsyncClient<CC> asyncClient;

                    ClientSpecificBuilder(final AsyncClient<CC> asyncClient) {
                        this.asyncClient = asyncClient;
                    }

                    public <C extends MoccaClient> C build(final Class<C> apiType) {
                        MoccaFeignEncoder encoder = new MoccaFeignEncoder();

                        AsyncFeign.AsyncBuilder<CC> builder = AsyncFeign.<CC>asyncBuilder()
                            .options(optionsBuilder.options().orElse(new Request.Options()))
                            .contract(new MoccaFeignContract())
                            .encoder(encoder)
                            .decoder(new MoccaFeignDecoder())
                            .invocationHandlerFactory(new MoccaFeignInvocationHandlerFactory());
                        if (asyncClient != null) {
                            builder = builder.client(asyncClient);
                        }
                        C client =  builder.target(apiType, graphQLUrlString);
                        encoder.setClient(client);
                        // the client object is needed in the encoder to perform
                        // bean validation for the request
                        return client;
                    }
                }
            }
        }

        public static class OptionsBuilder {
            private Optional<Request.Options> options = Optional.empty();

            public OptionsBuilder options(
                final Duration connectTimeout,
                final Duration readTimeout,
                final boolean followRedirects
            ) {
                this.options = Optional.of(new Request.Options(
                    connectTimeout.toMillis(), TimeUnit.MILLISECONDS,
                    readTimeout.toMillis(), TimeUnit.MILLISECONDS,
                    followRedirects
                ));
                return this;
            }

            Optional<Request.Options> options() {
                return this.options;
            }
        }

        abstract static class BaseBuilder<B extends Builder.BaseBuilder<B>> {

            protected final String graphQLUrlString;
            protected final Set<MoccaCapability> capabilities = new HashSet<>();

            protected BaseBuilder(final String serverBaseUrl) {
                // Setting GraphQL URL String
                Arguments.requireNonNull(serverBaseUrl);
                if (serverBaseUrl.endsWith("/")) {
                    graphQLUrlString = serverBaseUrl + "graphql";
                } else {
                    graphQLUrlString = serverBaseUrl + "/graphql";
                }
            }

            /**
             * Creates a Mocca client instance of the supplied {@code apiType} interface.
             *
             * @param apiType the client API class, which should extends {@link MoccaClient}
             * @param <C>     the client API type, which should extends {@link MoccaClient}
             * @return the Mocca client instance, implementing the provided client API
             */
            protected abstract <C extends MoccaClient> C build(final Class<C> apiType);

            /**
             * Adds a new {@link MoccaCapability} to be configured in this client builder. An example of Mocca
             * capability would be Micrometer support, as seen in the example below.
             * <br>
             * <pre><code>
             * io.micrometer.core.instrument.simple.SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
             *
             * BooksAppClient micrometerEnabledClient = MoccaClient.Builder
             *     .sync("localhost:8080/booksapp")
             *     .addCapability(new com.paypal.mocca.MoccaMicrometerCapability(meterRegistry))
             *     .build(BooksAppClient.class);
             * </code></pre>
             * <br>
             * Notice the code in this example requires Mocca library {@code com.paypal.mocca:mocca-micrometer}.
             *
             * @param capability the Mocca capability to be configured in this client builder
             * @return this builder
             */
            @SuppressWarnings("unchecked")
            public B addCapability(final MoccaCapability capability) {
                capabilities.add(capability);
                return (B) this;
            }

            /**
             * Removes all {@link MoccaCapability} configured in this client builder.
             *
             * @return this builder
             */
            @SuppressWarnings("unchecked")
            public B clearCapabilities() {
                capabilities.clear();
                return (B) this;
            }
        }
    }
}
