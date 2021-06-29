
# Release notes

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