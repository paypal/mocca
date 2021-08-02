package com.paypal.mocca.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to add HTTP headers to a Mocca client.
 * <br>
 * <br>
 * HTTP headers can be set in Mocca at client API or method level using {@link RequestHeader} annotation. When {@link RequestHeader} annotation is added at client API level, the header will be included across all requests made through that client. The example below shows a header added at client API level.
 * <pre><code>
 * &#064;RequestHeader("sampleheader:samplevalue")
 * public interface BooksAppClient extends MoccaClient {
 *
 *     ...
 *
 * }
 * </code></pre>
 * As seen in the example above, a header is set as a String, where the name is followed by a colon and the header value.
 * <br>
 * The example below shows the usage of multiple headers at method level, added as an array of Strings.
 * <pre><code>
 * public interface BooksAppClient extends MoccaClient {
 *
 *     &#064;Query
 *     &#064;RequestHeader({"sampleheader:samplevalue", "anotherheader:anothervalue"})
 *     Book getBookById(int bookId);
 *
 *     ...
 *
 * }
 * </code></pre>
 * All header values in above examples are defined statically. If the application needs to set header values dynamically, annotation {@link RequestHeaderParam} can be used with GraphQL operation method parameters, as seen in the example below. Notice the header value is identified using a placeholder surrounded by curly braces.
 * <pre><code>
 * public interface BooksAppClient extends MoccaClient {
 *
 *     &#064;Query
 *     &#064;RequestHeader("sampleheader: {headervalue}")
 *     Book getBookById(&#064;RequestHeaderParam("headervalue") String dynamicvalue, int bookId);
 *
 *     ...
 *
 * }
 * </code></pre>
 * A few important notes:
 * <ol>
 *     <li>Dynamic headers can only be used at the method level</li>
 *     <li>A method can have a mix of static and dynamic headers</li>
 *     <li>An application can have as many client API and method level headers as necessary</li>
 *     <li>If the same header is set at client API and method level, the one set at the method takes precedence</li>
 *     <li>If the same header is defined at the same method multiple times, all specified values will be set at the header value using comma as separator</li>
 * </ol>
 *
 * @see RequestHeaderParam
 * @author abprabhakar@paypal.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestHeader {

    /**
     * Array of headers in key:value format
     *
     * @return array of headers
     */
    String[] value() default {};

}