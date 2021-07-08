package com.paypal.mocca.functional;

import com.paypal.mocca.client.MoccaAsyncApache5Client;
import com.paypal.mocca.client.MoccaClient;
import com.paypal.mocca.client.MoccaOkHttpClient;
import com.paypal.mocca.client.MoccaExecutorHttpClient;
import com.paypal.mocca.client.MoccaMicrometerCapability;
import com.paypal.mocca.client.MoccaResilience4j;
import com.paypal.mocca.client.model.Book;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import okhttp3.OkHttpClient;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.Assert.fail;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Basic query tests
 */
public class MoccaQueryTest extends AbstractFunctionalTests {

    /**
     * Test basic graphql query on books
     */
    @Test
    public void testBasicQuerySync() {
        List<Book> books = client.books();
        runBasicQueryAndAssertions(client);
    }

    @Test
    public void testMicrometerSupport() throws Exception {
        final SimpleMeterRegistry reg = new SimpleMeterRegistry();
        final BooksAppClient micrometerEnabledClient =
            MoccaClient.Builder.sync(getBaseUri().toString())
                .addCapability(new MoccaMicrometerCapability(reg))
                .build(BooksAppClient.class);

        runBasicQueryAndAssertions(micrometerEnabledClient);

        final List<Meter> moccaMeters =
            reg.getMeters()
                .stream()
                .filter(m -> m.getId().getName().startsWith("mocca."))
                .collect(Collectors.toList());

        assertTrue(!moccaMeters.isEmpty(), "Expected mocca meters to be registered.");
    }

    private void runBasicQueryAndAssertions(final BooksAppClient bookClient) {
        List<Book> books = bookClient.books();

        checkResults(books);
    }

    @Test
    public void testBasicQueryExecutor() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        MoccaExecutorHttpClient<OkHttpClient> executorClient = new MoccaExecutorHttpClient<>(new MoccaOkHttpClient(), executorService);

        AsyncBooksAppClient asyncClient = MoccaClient.Builder
                .async(getBaseUri().toString())
                .connectionTimeout(1000)
                .readTimeout(1000)
                .client(executorClient)
                .build(AsyncBooksAppClient.class);

        List<Book> books = asyncClient.books().get();

        checkResults(books);
    }

    @Test
    public void testBasicQueryAsync() throws ExecutionException, InterruptedException {
        CloseableHttpAsyncClient apacheAsyncHttpClient = HttpAsyncClients
                .custom()
                .disableAuthCaching()
                .disableCookieManagement()
                .build();
        apacheAsyncHttpClient.start();

        MoccaAsyncApache5Client asyncHttpClient = new MoccaAsyncApache5Client(apacheAsyncHttpClient);

        AsyncBooksAppClient asyncClient = MoccaClient.Builder
                .async(getBaseUri().toString())
                .connectionTimeout(1000)
                .readTimeout(1000)
                .client(asyncHttpClient)
                .build(AsyncBooksAppClient.class);

        List<Book> books = asyncClient.books().get();

        checkResults(books);
    }

    @Test
    public void testResilientQuery() {
        final CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("marcels-cb");

        final BooksAppClient client =
            MoccaClient.Builder
                .sync(getBaseUri().toString())
                .resiliency(
                    new MoccaResilience4j.Builder()
                        .circuitBreaker(circuitBreaker)
                        .build()
                ).build(BooksAppClient.class);

        final Supplier<List<Book>> getBooks = () -> client.books();

        checkResults(getBooks.get());

        circuitBreaker.transitionToForcedOpenState();
        try {
            getBooks.get();
            fail("Expected circuit to be open");
        } catch (final CallNotPermittedException e) {
        }

        circuitBreaker.transitionToClosedState();
        checkResults(getBooks.get());
    }

    private void checkResults(List<Book> books) {
        assertNotNull(books);
        assertTrue(books.size() >= 2);
        //assert books are returned back
        assertTrue(books.stream().anyMatch(book -> book.getId() == 1));
        assertTrue(books.stream().anyMatch(book -> book.getId() == 2));
        assertTrue(books.stream().anyMatch(book -> "Book1".equals(book.getName())));
    }

}
