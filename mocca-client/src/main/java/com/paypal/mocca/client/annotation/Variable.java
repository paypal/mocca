package com.paypal.mocca.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used to configure the request DTO type,
 * used to define the GraphQL request variables.
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
@Target(ElementType.PARAMETER)
public @interface Variable {

    /**
     * When using a DTO to define GraphQL variables, sometimes the DTO object is populated with certain properties that the application would like to be ignored by Mocca when serializing the GraphQL variables.
     * In order to do so, specify here an array containing all fields to be ignored in the request DTO object. If the request type is not a DTO, then {@code ignore} will have no effect.
     *
     * @return an array containing all fields to be ignored in the request DTO object
     */
    String[] ignore() default "";

}