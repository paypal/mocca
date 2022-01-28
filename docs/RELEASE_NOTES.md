
# Release notes

## 0.0.6

#### Release date
January 28th, 2022

#### New features and enhancements
TBD

#### Bug fixes
1. [Fix classpath issue caused by conflicting versions of OkHttp](https://github.com/paypal/mocca/issues/61)

## 0.0.5

#### Release date
January 12th, 2022

#### New features and enhancements
None

#### Bug fixes
1. [Dependency jackson-modules-java8 should be marked as pom](https://github.com/paypal/mocca/issues/59)

## 0.0.4

#### Release date
January 8th, 2021

#### New features and enhancements
1. [Add support for Set data type inside POJOs used in the SelectionSet](https://github.com/paypal/mocca/issues/55)
1. [Upgrade key libraries to their latest available versions](https://github.com/paypal/mocca/issues/56)

#### Bug fixes
1. [Mocca should unwrap user facing exceptions from Feign exceptions and hide Feign exceptions](https://github.com/paypal/mocca/issues/40)
1. [Fix typo in mocca-resilience4j module name](https://github.com/paypal/mocca/issues/54)

## 0.0.3

#### Release date
September 21st, 2021

#### New features and enhancements
1. [Various mocca-client impls should get functionally tested](https://github.com/paypal/mocca/issues/32)
1. [Additional enhancements to support GraphQL variables and selection set data types corner cases](https://github.com/paypal/mocca/issues/30)
1. [Add support for Bean Validations annotations to validate GraphQL operation method parameters](https://github.com/paypal/mocca/issues/28)
1. [Enhance SelectionSet annotation to ignore fields in the return type](https://github.com/paypal/mocca/issues/27)
1. [Add support for operations with no variables by setting no parameters in the request method](https://github.com/paypal/mocca/issues/14)
1. [Mocca should support the usage of multiple method parameters as GraphQL variables](https://github.com/paypal/mocca/issues/13)
1. [Add support for Resilience4j Fallback](https://github.com/paypal/mocca/issues/11)
1. [JAXRS-based JagaHttpClient takes a JAXRS ClientBuilder, not Client.. ](https://github.com/paypal/mocca/issues/7)
1. [Hosted javadoc (UI)](https://github.com/paypal/mocca/issues/3)

#### Bug fixes
1. [Fix serialization and deserialization issues and make sure client honors documentation](https://github.com/paypal/mocca/issues/24)
1. [Implement a mechanism in Mocca serializer to avoid cycles in request and response](https://github.com/paypal/mocca/issues/15)

## 0.0.2

#### Release date
June 29th, 2021

#### New features and enhancements
1. Documentation enhancements

#### Bug fixes
1. Sonatype artifacts publishing bug fix

## 0.0.1

#### Release date
June 21st, 2021

#### New features and enhancements
1. GraphQL features
   1. GraphQL queries and mutations via annotations `@Query` and `@Mutation`
   1. Automatic variable definition based on DTO request type
   1. Automatic selection set definition based on DTO response type
   1. Annotation and String based custom input variables via annotation `@Variable`
   1. Annotation and String based custom selection set via annotation `@SelectionSet`
1. Static and dynamic HTTP request headers support via annotations `@RequestHeader` and `@RequestHeaderParam`
1. Observability via Micrometer
1. Resilience with via Resilience4J
1. Flexible API allowing the following pluggable HTTP clients
   1. OkHttp
   1. Apache HTTP client 5
   1. Apache HTTP client
   1. Google HTTP client
   1. Java HTTP2 client
   1. JAX-RS 2
1. Asynchronous support
   1. CompletableFuture
   1. Pluggable asynchronous HTTP clients
   1. User provided executor services

#### Bug fixes
None