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
 * {@code SelectionSet} would then behave like this:
 * <ol>
 *     <li>If annotation is present and its value attribute is set, Mocca automatic selection set resolution is turned off, and SelectionSet value is used to define the selection set. In this case if ignore value is also set, then that is not used by Mocca, and a warning is logged.</li>
 *     <li>If annotation is present, its value attribute is NOT set, but ignore is, then Mocca automatic selection set resolution is turned ON, and SelectionSet ignore is used to pick which response DTO fields to ignore from the selection set.</li>
 *     <li>If annotation is present and both value and ignore attributes are NOT set, then a MoccaException is thrown.</li>
 * </ol>
 *
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
 *     &#064;SelectionSet(ignore="title")
 *     List&#60;Book&#62; getBookById(int bookId);
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
     * When the Response type is a POJO (following Java bean conventions), sometimes it is populated with certain properties
     * the application would like to be ignored by Mocca when generating the GraphQL Selection Set.
     * In order to do so, specify here an array containing all fields to be ignored in the Selection Set.
     * The name of a property in an inner POJO can be specified using the outer field name followed by dot.
     * If the request type is not a POJO, or if {@link #value()} is set, then {@code ignore} will have no effect.
     * If the property set to be ignored doesn't exist, then it has no effect, the Selection set is generated normally as
     * if that ignore value had not been set.
     * If both {@link #value()} and {@code ignore} attributes are set, then the {@code ignore} value is not used by Mocca, and a warning is logged.
     *
     * @return an array containing all fields to be ignored in the return type, in case it is a DTO
     */
    String[] ignore() default UNDEFINED;

}
