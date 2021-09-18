
<br><br>
<div style="text-align:center"><img src="img/logo/mocca_logo_horizontal_lightbluebackground.png" style="background-color:#27a1f2; padding:40px;"/></div>
<br>

# Mocca end user documentation (version 0.0.3)

## Summary

**1 Introduction<br>
2 Quick start<br>
3 Client dependencies<br>
4 API definition<br>
5 Code generation<br>
6 Client build and configuration<br>
7 Asynchronous development**

## 1 Introduction

### 1.1 What is Mocca?

Mocca is a GraphQL client for JVM languages with the goal of being easy to use, flexible and modular. With that in mind, Mocca was designed to offer:

1. Simple and intuitive API
1. Good end user documentation
1. Pluggable HTTP clients
1. Pluggable components, relying on great open source libraries, allowing features such as code generation, resilience, parsers and observability

### 1.2 Mocca main features

Mocca offers support for:

1. GraphQL features
    1. GraphQL queries and mutations
    1. Automatic variable definition
    1. Automatic selection set definition based on DTO response type
    1. Annotation and String based custom input variables
    1. Annotation and String based custom selection set
1. Static and dynamic HTTP request headers
1. Observability via Micrometer
1. Resilience with via Resilience4J
1. Flexible API allowing various pluggable HTTP clients
1. Asynchronous support
    1. CompletableFuture
    1. Pluggable asynchronous HTTP clients
    1. User provided executor services

## 2 Quick start

This section shows a simple example of how to use Mocca for a quick start. For further information, please read the other sections in this document.

### 2.1 Add mocca-client dependency

Add dependency `com.paypal.mocca:mocca-client:0.0.3` to your application, as shown below (examples for Maven and Gradle).

``` Groovy
// Adding Mocca in Gradle
dependencies {
    compile 'com.paypal.mocca:mocca-client:0.0.3'
}
```

``` XML
<!-- Adding Mocca in Maven -->
<dependency>
    <groupId>com.paypal.mocca</groupId>
    <artifactId>mocca-client</artifactId>
    <version>0.0.3</version>
</dependency> 
```

### 2.2 Create a client API

Create a new Java interface, extend `MoccaClient`, and add Mocca annotated methods to it representing all necessary GraphQL queries and mutations (according to server API), as seen in the example below.

``` java

import com.paypal.mocca.client.MoccaClient;
import com.paypal.mocca.client.annotation.Mutation;
import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.SelectionSet;
import com.paypal.mocca.client.annotation.Var;

public interface BooksAppClient extends MoccaClient {

    @Query
    @SelectionSet("{id, name}")
    List<Book> getBooks(@Var("authorId") long authorId);

    @Query
    Book getBook(@Var("id") long id);

    @Mutation
    Author addAuthor(@Var(value = "author", ignore = "books") Author author);

    @Mutation
    Book addBook(@Var("book") Book book);

}
```

### 2.3 Build a client and start using it

``` java
// Building a new Mocca client
BooksAppClient client = MoccaClient.Builder
    .sync("http://localhost:8080/booksapp")
    .build(BooksAppClient.class);

// Getting a book by id
Book book = client.getBook(123);    
```

### 2.4 Exploring other Mocca features

For more information about how to use Mocca, using specific HTTP clients, asynchronous programming, and others, please read the other sections in this document.

## 3 Client dependencies

The main dependency needed to use Mocca is `com.paypal.mocca:mocca-client:0.0.3`. Below you can see two examples of how to add it to your application, depending on whether you use Gradle or Maven. Other Mocca dependencies may be necessary, depending on the application needs, and will be covered in other sections.

``` Groovy
// Adding Mocca in Gradle
dependencies {
    compile 'com.paypal.mocca:mocca-client:0.0.3'
}
```

``` XML
<!-- Adding Mocca in Maven -->
<dependency>
    <groupId>com.paypal.mocca</groupId>
    <artifactId>mocca-client</artifactId>
    <version>0.0.3</version>
</dependency> 
```

## 4 API definition

### 4.1 How to create the client-side API types?

In order to call a GraphQL service, in simple terms, it is usually necessary to have:

1. Depending on the type of client used, an interface representing the operations (similar to a stub in a remote procedure call)
1. Optionally, DTO or POJO classes representing the operation variables (the input arguments)
1. DTO classes representing the response

The classes mentioned above can usually be obtained by using a code generation tool, writing them from scratch, or by using a preexistent library (usually provided by the team who owns the service, but not necessarily).

Mocca supports all three approaches. However, in order to determine which one is the most appropriate for each application, it is important first to understand the possible different situations when using a GraphQL client in a Java application. Some of them are listed below:

1. **Same team, client and server Java**: The server application is owned by the same team who owns the client application, and both are written in Java.
1. **Different teams, but client and server Java**: The server application is **not** owned by the same team who owns the client application, but there is a Java library available (often provided and maintained by the team who owns the server application) with the DTOs and auxiliary types used in the server GraphQL contract.
1. **Different teams, only client Java**: The server application is **not** owned by the same team who owns the client application, there is not a Java library available with the DTOs and auxiliary types used in the server GraphQL contract, but the GraphQL schema is available.

Although a code generation approach can be used in all situations described above, and there are definitely benefits in doing so, many times it will be more convenient, simpler and quicker to rely on a manually created library (preexistent or not) with the required types. Different than other JVM GraphQL clients, Mocca takes that into consideration and doesn't force the developer to necessarily generate code, which many times may be too tedious or complex.

The following subsections describe how to write the necessary classes and interfaces in order to define the client API and create a Mocca client to call a GraphQL server.

The use of a code generation tool, and how to use it with Mocca, is covered in another section, later on in this document.

### 4.2 What is a client API?

A client API is a Java interface containing abstract methods representing the GraphQL service operations acessible to the application. You can see more details about it, and examples, in the next subsection.

This interface then is used by the application to define a variable whose instance is provided by Mocca client builder, as seen in the example below (`BooksAppClient` is the client API).

``` java
BooksAppClient client = MoccaClient.Builder
    .sync("http://localhost:8080/booksapp")
    .build(BooksAppClient.class);
```

### 4.3 Writing a client API

The most basic rules when writing a client API are listed below. Other rules, associated with Mocca's more advanced features, are explained in the following subsections.

1. The Java interface must extend `com.paypal.mocca.client.MoccaClient`
1. The signature of each GraphQL operation method must:
   1. Have zero or more parameters to represent the variables section declared in the GraphQL schema of same operation. All parameters representing a GraphQL variable must be annotated with `com.paypal.mocca.client.annotation.Var`, used to name the variable, plus additional optional configuration. The parameter name is not relevant, the GraphQL variable name is defined in the annotation, but its type must be one of the following:
      1. Any primitive type, or primitive wrapper
      1. `java.lang.String`
      1. A POJO following Java beans conventions
      1. A `java.util.List` or `java.util.Optional` whose type can be any of the types mentioned earlier (except primitives)
   1. Declare a return type (it cannot be `void`) analog to the type declared in the GraphQL schema of same operation. This return type must be one of the following:
      1. Any primitive type, or primitive wrapper
      1. A DTO
      1. A `java.util.List` or `java.util.Optional` whose type can be a DTO or a primitive wrapper
1. Each GraphQL operation method must be annotated with a `Query` or `Mutation` annotation (from package `com.paypal.mocca.client.annotation`) depending on whether the operation is a GraphQL query or mutation respectively.
1. The method name should be the same as the GraphQL operation name. If a different name is desired for the Java method, then the GraphQL operation name must be set using the `name` attribute in the `Query` or `Mutation` annotation.

You can see below an example of a simple client API.

``` java

import com.paypal.mocca.client.MoccaClient;
import com.paypal.mocca.client.annotation.Mutation;
import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.Var;

public interface BooksAppClient extends MoccaClient {

    @Query
    List<Book> getBooks(@Var("authorId") long authorId);

    @Query
    Book getBook(@Var("id") long id);

    @Mutation
    Author addAuthor(@Var(value = "author", ignore = "books") Author author);

    @Mutation
    Book addBook(@Var("book") Book book);

}
```

### 4.4 Specifying GraphQL variables

According to GraphQL specification, *"A GraphQL query can be parameterized with variables, maximizing query reuse, and avoiding costly string building in clients at runtime"*.

Mocca offers different ways to define the GraphQL variables. They are listed below and explained in details in the next subsections.

1. Using one or more parameters, each representing a GraphQL variable
1. Using a single String containing all GraphQL variables

#### 4.4.1 Setting GraphQL variables dynamically

One of the most convenient ways to dynamically define GraphQL operation variables in Mocca is by specifying each variable as a method parameter.

Its usage is very simple, just annotate each parameter in the GraphQL operation method (annotated with `com.paypal.mocca.client.annotation.Query` or `com.paypal.mocca.client.annotation.Mutation`) with `com.paypal.mocca.client.annotation.Var`, as seen below:


``` java
@Mutation
Author addAuthor(@Var("name") String name, @Var("books") List<Book> books);
```

The name of the variable must always be set in the `Var` annotation, except if `raw` is set to true, as explained in the next subsection. The parameter name is not relevant (as the GraphQL variable name is defined in the annotation), but its type must be one of the following:

1. Any primitive type, or primitive wrapper
1. `java.lang.String`
1. A POJO following Java beans conventions
1. A `java.util.List` whose type can be any of the types mentioned earlier (except primitives)

Additional optional configuration may also be provided, and are explained in next subsections.

If any POJO property is `null` (returned from its getter method), then it is not included in the GraphQL variables. Also, if any field is another POJO, then it is processed as a Java bean as well, and its properties are written in GraphQL within curly brackets.

#### 4.4.2 Setting GraphQL variables statically

If it is preferable to define the GraphQL operation variables statically, a String object can be passed as argument in the GraphQL operation method, as seen in the API below:


``` java
/**
 * Adds an author and returns its id
 */
@Mutation
Author addAuthor(@Var(raw = true) String variables);
```

Below it is shown how an application would use this operation. Notice `raw` property must be set to true, and there is no need to wrap the value with parenthesis.

``` java
BooksAppClient client = MoccaClient.Builder
    .sync("http://localhost:8080/booksapp")
    .build(BooksAppClient.class);

Author author = client.addAuthor("name: \"Carlos Drummond de Andrade\", books: [{title: \"Alguma poesia\"}, {title: \"A rosa do povo\"}]");
```

The GraphQL variables passed as argument could also refer to complex types, by making usage of curly braces.

#### 4.4.3 Defining no variables

If the GraphQL operation requires no variables, then its method in the API can simply have no parameters neither. See the example below.

``` java
/**
 * Returns a book recommendation for the logged in user
 */
@Query
Book getBookRecommendation();
```

And this is how an application could use this operation:

``` java
BooksAppClient client = MoccaClient.Builder
    .sync("http://localhost:8080/booksapp")
    .build(BooksAppClient.class);

Book recommendedBook = client.getBookRecommendation();
```

In this case Mocca produces a GraphQL query without any variables.

#### 4.4.4 Ignoring POJO fields

When using POJOs to define GraphQL variables, sometimes the POJO is populated with certain properties that the application would like to be ignored by Mocca when serializing the GraphQL variables.

That can be configured as follows during API definition:

``` java
/**
 * Adds an author and returns its id
 */
@Mutation
int addAuthor(@Var(value = "author", ignore = "books") Author author);
```

In the example above, when calling `addAuthor`, Mocca will not include the list of books as variable in the payload of the GraphQL message. 
For a slightly more complicated example, let's say you wanted to add a book along with the book's author but don't want to include the list
of books written by the author. You would use the `ignore` property like this:

```java
public class Author {
    String name;
    Book[] books;
    
}
   
/**
 * Adds a book with the author but without the list of books written by the author
 */
@Mutation
int addBook(@Var(value = "book", ignore = "author.books") Book book);
```
In this case the `books` field in the `author` sub-object would be omitted.

### 4.5 Specifying GraphQL selection set

Here is how GraphQL specification introduces the notion of selection set:

*"An operation selects the set of information it needs, and will receive exactly that information and nothing more, avoiding over‐fetching and under‐fetching data."*

Mocca offers different ways to define the selection set of a GraphQL operation. They are listed below and explained in details in the next subsections.

1. Using Mocca automatic resolution
1. Using Mocca `SelectionSet` annotation

#### 4.5.1 Using Mocca automatic resolution

Mocca will automatically define the GraphQL operation selection set by using the data transfer object (DTO) used as return type set in the method signature.

It is important to state though that this DTO class must be a Java bean, and that is how Mocca figures out which values to use when writing the GraphQL selection set.

See in the example below the API definition, the DTO used as returned type and a sample of a GraphQL query, and its selection set, generated automatically by Mocca behind the scenes based on the DTO class.

Notice in the example below `Author` class is omitted for brevity, but it is also a Java bean.

##### Return DTO definition

``` java
public class Book {

    private int id;
    private Author author;
    private String title:

    // getters and setters omitted for brevity

}
```

##### GraphQL operation definition in the client API

``` java
@Query
Book getBookById(int bookId);
```

##### Sample GraphQL query generated by Mocca

``` GraphQL
query {
  getBookById(bookId: 1000) {
    id
    author {
      id
      name
    }
    title
  }
}
```

#### 4.5.2 Using Mocca SelectionSet annotation

There might be cases when the application has a DTO it could use as return type for a GraphQL operation, however, not all its fields are really required to be returned from the server. In this case, the annotation `com.paypal.mocca.client.annotation.SelectionSet` can be used to customize the selection set, letting the application specify exactly what the data it would like to receive or ignore.

Mocca `SelectionSet` supports two options:

- value
- ignore

`SelectionSet` behaviour:

- If annotation is present and its `value` attribute is set, Mocca automatic selection set resolution is turned off, and `SelectionSet` `value` is used to define the selection set. In this case if `ignore` value is also set, then that is not used by Mocca, and a warning is logged.
- If annotation is present, its value attribute is NOT set, but ignore is, then Mocca automatic selection set resolution is turned ON, and `SelectionSet` `ignore` is used to pick which response DTO fields to ignore from the selection set.
- If annotation is present and both value and ignore attributes are NOT set, then a `MoccaException` is thrown.

It is important to mention though that, when `SelectionSet` annotation is present, although Mocca won't resolve automatically the selection set using the return type, still application has to make sure all fields in the provided custom selection set exist in the DTO used in the return type.

In the example below application wants to use the `Book` DTO as return type, but it doesn't need the `Author` data, so its custom selection set omits it. Notice `SelectionSet` value must be wrapped around curly braces

##### Return DTO definition

``` java
public class Book {

    private int id;
    private Author author;
    private String title:

    // getters and setters omitted for brevity
	
}
```

##### GraphQL operation definition in the client API

``` java
@Query
@SelectionSet({id, title})
Book getBookById(int bookId);
```

##### Sample GraphQL query generated by Mocca

``` GraphQL
query {
  getBookById(bookId: 1000) {
    id
    title
  }
}
```

The example below shows an application using `Book` DTO as return type, but ignoring only the `title` field. Notice multiple values can be set to `SelectionSet` `ignore` as an array of `String` values.

##### GraphQL operation definition in the client API

``` java
@Query
@SelectionSet(ignore="title")
Book getBookById(int bookId);
```

##### Sample GraphQL query generated by Mocca

``` GraphQL
query {
  getBookById(bookId: 1000) {
    id
    Author
  }
}
```

## 5 Code generation

Code generation for a GraphQL service in Java can be done for both server side and client side. There are few code generation tools available on opensource out of which [Apollo Code Generator](https://the-guild.dev/blog/graphql-codegen-java) and [Netflix DGS framework](https://netflix.github.io/dgs/generating-code-from-schema/) ones are popular.

For Client side code generation, all these generators generate Request/Response classes and model classes. With Mocca's innovative programming model request and response classes are not required. Service side code generation is out of scope for Mocca.

However, generated DTO classes (sometimes referred as "model classes", or also POJOs) can still be used with Mocca client. An example of using code generator for generating model classes is provided on [mocca-functional-tests](../mocca-functional-tests/build.gradle) build script. Mocca example uses [kobylynskyi graphql codegen](https://github.com/kobylynskyi/graphql-java-codegen) as it allows flexible configuration and extension.

Below you can see a code block where code generation is configured using kobylynskyi and Gradle.

``` groovy
compileJava.dependsOn "graphqlCodegenClient"
sourceSets.main.java.srcDir "$buildDir/generated-client"
task graphqlCodegenClient(type: GraphQLCodegenGradleTask) {
    graphqlSchemas.rootDir = "$projectDir/src/main/resources/schema"
    outputDir = new File("$buildDir/generated-client")
    modelPackageName = "com.paypal.mocca.client.model"
    generateApis = false
    generateClient = false
    generateParameterizedFieldsResolvers = false
    modelValidationAnnotation = ""
    addGeneratedAnnotation = false
}
```

## 6 Client build and configuration

### 6.1 Building a client

The code block below shows how to create a Mocca client with the minimum required configuration:

``` java
BooksAppClient client = MoccaClient.Builder
    .sync("http://localhost:8080/booksapp")
    .build(BooksAppClient.class);
```

Notice a builder is used, and there are two flavors of clients available, synchronous and asynchronous. In the example above a synchronous client, for regular blocking single-threaded programming, is created.

The method to create the builder also takes the server base URL as parameter. This String must contain the URL all the way to the `graphql` endpoint, without including it in the URI path (that is added automatically by Mocca behind the scenes).

The actual client instance is then created by calling the `build` method, which takes as parameter the client API interface, whose definition was already explained earlier in this document.

### 6.2 Choosing the HTTP client

Mocca uses behind the scenes an HTTP client to make the GraphQL calls. By default, JDK `java.net.HttpURLConnection` is used as HTTP client, and no additional dependency is required to use it.

However, if preferred, a custom HTTP client can be specified by adding an extra Mocca dependency and setting the HTTP client when creating the Mocca client builder (using method `client`), as seen below:

``` java
BooksAppClient client = MoccaClient.Builder
    .sync("http://localhost:8080/booksapp")
    .client(new MoccaOkHttpClient())
    .build(BooksAppClient.class);
```

All HTTP clients supported by Mocca are documented in the table below, along with the library to be added as dependency to the application, and the client class to be used in the Mocca builder.

| HTTP client  | Dependency | Client class | Notes |
| :-------------: | :-------------: | :-------------: | :-------------: |
| **OkHttp client**  | `com.paypal.mocca:mocca-okhttp:0.0.3` | `com.paypal.mocca.client.MoccaOkHttpClient` | |
| **Apache HTTP client 5**  | `com.paypal.mocca:mocca-hc5:0.0.3` | `com.paypal.mocca.client.MoccaApache5Client` | Apache HTTP client 5 |
| **Apache HTTP client**  | `com.paypal.mocca:mocca-apache:0.0.3` | `com.paypal.mocca.client.MoccaApacheClient` | Original Apache HTTP client |
| **Google HTTP client**  | `com.paypal.mocca:mocca-google:0.0.3` | `com.paypal.mocca.client.MoccaGoogleHttpClient` | |
| **Java HTTP2 client**  | `com.paypal.mocca:mocca-http2:0.0.3` | `com.paypal.mocca.client.MoccaHttp2Client` | HTTP2 client provided by the JDK |
| **JAX-RS 2 client**  | `com.paypal.mocca:mocca-jaxrs2:0.0.3` | `com.paypal.mocca.client.MoccaJaxrsClient` | |

The table above includes only synchronous clients, and the code samples in this section are specific to synchronous clients. For information about supported asynchronous clients, and how to configure it, please read section **Asynchronous development**.

All Mocca classes mentioned in the table above work as a wrapper containing a default instance of the HTTP client. If the application needs the HTTP client to be configured in a certain manner, there are two ways to achieve that:

1. Mocca client builder could be used to do so, providing a common API regardless of the type of HTTP client used.
1. A custom instance of the HTTP client could be provided as a constructor parameter to the Mocca HTTP client wrapper.

``` java
okhttp3.OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
    .readTimeout(2, TimeUnit.SECONDS)
    .build();

BooksAppClient client = MoccaClient.Builder
    .sync("localhost:8080/booksapp")
    .client(new MoccaOkHttpClient(okHttpClient))
    .build(BooksAppClient.class);
```

### 6.3 Setting HTTP headers

HTTP headers can be set in Mocca at client API or method level using `com.paypal.mocca.client.annotation.RequestHeader` annotation. When `@RequestHeader` annotation is added at client API level, the header will be included across all requests made through that client. The example below shows a header added at client API level.

``` java
@RequestHeader("sampleheader:samplevalue")
public interface BooksAppClient extends MoccaClient {

    ...

}
```

As seen in the example above, a header is set as a String, where the name is followed by a colon and the header value.

The example below shows the usage of multiple headers at method level, added as an array of Strings.

``` java
public interface BooksAppClient extends MoccaClient {

    @Query
    @RequestHeader({"sampleheader:samplevalue", "anotherheader:anothervalue"})
    Book getBookById(int bookId);

    ...

}
```

All header values in above examples are defined statically. If the application needs to set header values dynamically, annotation `com.paypal.mocca.client.annotation.RequestHeaderParam` can be used with GraphQL operation method parameters, as seen in the example below. Notice the header value is identified using a placeholder surrounded by curly braces.

``` java
public interface BooksAppClient extends MoccaClient {

    @Query
    @RequestHeader("sampleheader: {headervalue}")
    Book getBookById(@RequestHeaderParam("headervalue") String dynamicvalue, int bookId);

    ...

}
```

A few important notes:

1. Dynamic headers can only be used at the method level
1. A method can have a mix of static and dynamic headers
1. An application can have as many client API and method level headers as necessary
1. If the same header is set at client API and method level, the one set at the method takes precedence
1. If the same header is defined at the same method multiple times, all specified values will be set at the header value using comma as separator

### 6.4 Gathering metrics

Mocca supports [Micrometer](https://micrometer.io/)-based metrics via the optional library `com.paypal.mocca:mocca-micrometer:0.0.3`. These metrics primarily revolve around HTTP interactions with the target GraphQL server. The metrics have identifiers that start with `mocca.`.

The example below shows how to enable metric gathering in Mocca using Micrometer:

``` java
import com.paypal.mocca.client.MoccaMicrometerCapability;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

...

SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    
BooksAppClient micrometerEnabledClient = MoccaClient.Builder
    .sync("localhost:8080/booksapp")
    .addCapability(new MoccaMicrometerCapability(meterRegistry))
    .build(BooksAppClient.class);
```

### 6.5 Configuring resilience

Mocca supports [Resilience4j](https://github.com/resilience4j/resilience4j)-based resilience features via the optional library `com.paypal.mocca:mocca-resilience4j:0.0.3`.

The example below shows how to configure resilience features in a Mocca client using Resilience4j:

``` java
import com.paypal.mocca.client.MoccaResilience4j;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;

...

CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("BooksAppClient-cb");

MoccaResilience4j moccaResilience = new MoccaResilience4j.Builder()
    .circuitBreaker(circuitBreaker)
    .build();

BooksAppClient client = MoccaClient.Builder
    .sync("localhost:8080/booksapp")
    .resiliency(moccaResilience)
    .build(BooksAppClient.class);
```

Notice that a Mocca builder is used to create a `MoccaResilience4j` object containing the application specific Resilience4j configuration.

A few important notes:
1. Mocca Resilience4j feature is only supported by clients created with the `sync` builder (as seen in the example above). Clients created with Mocca `async` builder don't support Resilience4j at the moment.
1. Although the example above only shows the setting of a circuit breaker, the following additional Resilience4j features are supported.
   1. Retry
   2. Rate limiting
   3. Bulkhead
   4. Fallback
   
1. The order of registering each resilience feature in `MoccaResilience4j` matters. More details at the next subsection.

#### 6.5.1 Resilience features execution order

It is very important to state that the order in which resilience features are registered dictates the execution order.

As an example, in the sample code below, `rateLimiter` in `moccaResilience1` will be executed before `circuitBreaker`. In other words, even if the circuit is open, the rate of calls will still be controlled and limited. However, in `moccaResilience2`, once the circuit is open, rate limiting will not be executed.

``` java
MoccaResilience4j moccaResilience1 = new MoccaResilience4j.Builder()
    .rateLimiter(rateLimiter)
    .circuitBreaker(circuitBreaker)
    .fallback(fallback)
    .build();

MoccaResilience4j moccaResilience2 = new MoccaResilience4j.Builder()
    .circuitBreaker(circuitBreaker)
    .rateLimiter(rateLimiter)
    .fallback(fallback)
    .build();
```

Notice the instantiation and configuration of `rateLimiter` and `circuitBreaker` were omitted for brevity.

## 7 Asynchronous development

### 7.1 Defining the API for asynchronous development

Mocca supports asynchronous development by leveraging Java `CompletableFuture`. Because of that, when using a Mocca async client, the GraphQL operation methods in the client API must return `java.util.concurrent.CompletableFuture`, as seen in the example below.

``` java

import com.paypal.mocca.client.MoccaClient;
import com.paypal.mocca.client.annotation.Mutation;
import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.SelectionSet;

public interface AsyncBooksAppClient extends MoccaClient {

    @Query
    @SelectionSet("{id, name}")
    CompletableFuture<List<Book>> getBooks(String variables);

    @Query
    CompletableFuture<Book> getBook(long id);

    @Mutation
    CompletableFuture<Author> addAuthor(Author author);

    @Mutation
    CompletableFuture<Book> addBook(Book book);

}
```

It is important to emphasize that if a client API is used with an async HTTP client, then all its methods must be asynchronous (return `CompletableFuture`), while all methods in an API used in a sync client must be synchronous (not return `CompletableFuture`).

### 7.2 Configuring the client

Mocca supports two types of asynchrounous development approaches:

1. Using an async HTTP client
1. Using a sync HTTP client executed by an user provided executor service

Each one of them are described in details in the next subsections.

#### 7.2.1 Using an async HTTP client

The table below shows all async HTTP clients supported by Mocca, followed by an example of how to configure a Mocca async client.

| HTTP client  | Dependency | Client class |
| :-------------: | :-------------: | :-------------: |
| **Apache HTTP client 5**  | `com.paypal.mocca:mocca-hc5:0.0.3` | `com.paypal.mocca.client.MoccaAsyncApache5Client` |

``` java
MoccaAsyncApache5Client asyncHttpClient = new MoccaAsyncApache5Client();

BooksAppClient asyncClient = MoccaClient.Builder
    .async("http://localhost:8080/booksapp")
    .client(asyncHttpClient)
    .build(AsyncBooksAppClient.class);
```

Notice a different Mocca builder method is used in this case (`MoccaClient.Builder.async`). Similar to Mocca synchronous clients, the application can also provide configuration using the HTTP client object directly or using the builder settings, as seen below.

``` java
CloseableHttpAsyncClient apacheAsyncHttpClient = HttpAsyncClients
    .custom()
    .disableAuthCaching()
    .disableCookieManagement()
    .build();
apacheAsyncHttpClient.start();

MoccaAsyncApache5Client asyncHttpClient = new MoccaAsyncApache5Client(apacheAsyncHttpClient);

BooksAppClient asyncClient = MoccaClient.Builder
    .async("http://localhost:8080/booksapp")
    .client(asyncHttpClient)
    .build(AsyncBooksAppClient.class);
```

#### 7.2.3 Using a sync HTTP client run by an executor service

The example below shows how to configure a Mocca async client using a regular Mocca sync client (read **Client build and configuration**), but run by an application-provided executor service.

``` java
ExecutorService executorService = Executors.newCachedThreadPool();
MoccaExecutorHttpClient<OkHttpClient> executorClient = new MoccaExecutorHttpClient<>(new MoccaOkHttpClient(), executorService);

AsyncBooksAppClient asyncClient = MoccaClient.Builder
    .async("localhost:8080/booksapp")
    .client(executorClient)
    .build(AsyncBooksAppClient.class);
```

Notice that Mocca asynchronous builder is used in this case (`MoccaClient.Builder.async`).

The example below shows an application providing custom configuration using the HTTP client object directly and, for illustration purposes, using the builder settings as well.

``` java
okhttp3.OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
    .readTimeout(1, TimeUnit.SECONDS)
    .build();

ExecutorService executorService = Executors.newCachedThreadPool();

MoccaExecutorHttpClient<OkHttpClient> executorClient = new MoccaExecutorHttpClient<>(new MoccaOkHttpClient(), executorService);

AsyncBooksAppClient asyncClient = MoccaClient.Builder
        .async("localhost:8080/booksapp")
        .client(executorClient)
        .build(AsyncBooksAppClient.class);
```
