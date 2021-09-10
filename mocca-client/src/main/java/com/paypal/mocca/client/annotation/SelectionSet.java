package com.paypal.mocca.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used to define a GraphQL selection set,
 * to be used in combination with an operation annotation
 * in a method in a client API.
 * <br>
 * When {@code SelectionSet} annotation is present, although Mocca won't resolve automatically the selection set using the return type, still the application has to make sure all fields in the provided custom selection set exist in the DTO used in the return type.
 * <br>
 * See a client API example below. Notice the given selection set must be wrapped around curly braces.
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
public @interface SelectionSet {

    String UNDEFINED = "UNDEFINED";

    /**
     * The actual GraphQL selection set in plain text.
     * The given selection set must be wrapped around curly braces.
     *
     * @return the user provided selection set
     */
    String value() default UNDEFINED;

    /**
     * The fields to be ignored from the selection set.
     *
     *
     * @return an array containing all fields to be ignored in a POJO variable
     */
    String[] ignore() default UNDEFINED;

}
