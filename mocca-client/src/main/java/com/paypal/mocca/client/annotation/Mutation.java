package com.paypal.mocca.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used to define a GraphQL mutation
 * and its configuration in a client API.
 * <br>
 * See the client API example below.
 * <pre><code>
 * import com.paypal.mocca.client.MoccaClient;
 * import com.paypal.mocca.client.annotation.Mutation;
 * import com.paypal.mocca.client.annotation.Query;
 * import com.paypal.mocca.client.annotation.SelectionSet;
 *
 * public interface BooksAppClient extends MoccaClient {
 *
 *     &#064;Query
 *     &#064;SelectionSet("{id, name}")
 *     List&#60;Book&#62; getBooks(String variables);
 *
 *     &#064;Query
 *     Book getBook(long id);
 *
 *     &#064;Mutation
 *     Author addAuthor(@Variable(ignore = "books")Author author);
 *
 *     &#064;Mutation
 *     Book addBook(Book book);
 *
 * }</code></pre>
 *
 * @author fabiocarvalho777@gmail.com
 */
@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface Mutation {

    String UNDEFINED = "UNDEFINED";

    /**
     * Used to provide a custom name to the GraphQL mutation.
     * If not set, the operation method name will be used as mutation name.
     *
     * @return a custom name set to the GraphQL mutation
     */
    String name() default UNDEFINED;

}