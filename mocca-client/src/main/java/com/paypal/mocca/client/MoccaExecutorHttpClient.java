package com.paypal.mocca.client;

import feign.AsyncClient;

import java.util.concurrent.ExecutorService;

/**
 * A Mocca async HTTP client based on a Mocca sync HTTP client
 * whose executions are delegated to, and run by,
 * an user provided {@link ExecutorService}.
 * <br>
 * An example of how to use it can be seen below.
 * In this example, {@code AsyncBooksAppClient} is the application defined client API,
 * where all GraphQL operation methods return {@link java.util.concurrent.CompletableFuture}.
 *
 * <pre>
 * {@code
 *         ExecutorService executorService = Executors.newCachedThreadPool();
 *         MoccaExecutorHttpClient<OkHttpClient> executorClient = new MoccaExecutorHttpClient<>(new MoccaOkHttpClient(), executorService);
 *
 *         AsyncBooksAppClient client = MoccaClient.Builder
 *                 .async(getBaseUri().toString())
 *                 .client(executorClient)
 *                 .build(AsyncBooksAppClient.class);
 * }
 * </pre>
 *
 * @param <C> an optional context
 * @author fabiocarvalho777@gmail.com
 */
public final class MoccaExecutorHttpClient<C> extends MoccaAsyncHttpClient.WithRequestTimeouts<C> {

    /**
     * A Mocca async HTTP client based on a Mocca sync HTTP client
     * whose executions are delegated to, and run by,
     * an user provided {@link ExecutorService}
     *
     * @param moccaHttpClient the Mocca sync HTTP client to perform the GraphQL remote calls
     * @param executorService the executor service in charge of running all GraphQL remote calls
     */
    public MoccaExecutorHttpClient(MoccaHttpClient moccaHttpClient, ExecutorService executorService) {
        super(new AsyncClient.Default<>(moccaHttpClient.getFeignClient(), executorService));
    }

}