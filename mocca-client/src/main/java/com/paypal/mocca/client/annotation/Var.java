package com.paypal.mocca.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used to configure GraphQL request variables.
 * <br>
 * This annotation must be used in every parameter present in the operation method. All parameters together represent the variables section declared in the GraphQL schema of same operation.
 * A parameter name is not relevant, as the GraphQL variable name is defined in this annotation, but its type must be one of the following:
 * <ol>
 *    <li>Any primitive type, or primitive wrapper
 *    <li><code>java.lang.String</code>
 *    <li>A POJO following Java beans conventions
 *    <li>A <code>java.util.List</code> whose type can be any of the types mentioned earlier (except primitives)
 * </ol>
 * <br>
 * See the client API example below.
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
@Retention(RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Var {

    /**
     * Used to provide the name to the GraphQL variable.
     * This property is mandatory and must not be blank,
     * except when {@link #raw()} is set to true.
     *
     * @return the name of the GraphQL variable.
     */
    String value() default "";

    /**
     * When the variable is a POJO (following Java bean conventions), sometimes it is populated with certain properties
     * the application would like to be ignored by Mocca when serializing the GraphQL variables.
     * In order to do so, specify here an array containing all fields to be ignored in the POJO object.
     * The name of a property in an inner POJO can be specified using the outer field name followed by dot.
     * If the request type is not a POJO, or if {@link #raw()} is set to true, then {@code ignore} will have no effect.
     *
     * @return an array containing all fields to be ignored in a POJO variable
     */
    String[] ignore() default "";

    /**
     * It might be useful in certain cases to specify the whole GraphQL variables section as a String,
     * containing all variables inside of it, following the GraphQL specification.
     * In cases like this, this flag has to be set to true, only one parameter should be present,
     * and its type must be String. There is no need to wrap it with parenthesis though, as seen in the example below.
     * This feature is set to false by default.
     * <pre><code>
     *
     *     &#064;Query
     *     &#064;SelectionSet("{id, name}")
     *     List&#60;Book&#62; getBooks(&#064;Var("authorId") String query);
     *
     * </code></pre>
     *
     * @return whether all GraphQL variables should be set using one String parameter
     */
    // TODO This feature is not implemented yet
    boolean raw() default false;

}