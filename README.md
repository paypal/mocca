[![Build Status](https://travis-ci.com/paypal/mocca.svg?branch=master)](https://travis-ci.com/paypal/mocca)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.paypal.mocca/mocca-client/badge.svg?style=flat)](http://search.maven.org/#search|ga|1|g:com.paypal.mocca)
[![javadoc](https://javadoc.io/badge2/com.paypal.mocca/mocca-client/javadoc.svg)](https://javadoc.io/doc/com.paypal.mocca/mocca-client)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

<br><br>
<div style="text-align:center"><img src="docs/img/logo/mocca_logo_horizontal.png"/></div>
<br>

# Mocca

Mocca is a GraphQL client for JVM languages with the goal of being easy to use, flexible and modular. With that in mind, Mocca was designed to offer:

1. Simple and intuitive API
1. Good end user documentation
1. Pluggable HTTP clients
1. Pluggable components, relying on great open source libraries, allowing features such as code generation, resilience, parsers and observability

## Features

Mocca offers support for:

1. GraphQL features
    1. GraphQL queries and mutations
    1. Automatic variable definition
    1. Automatic selection set definition based on DTO response type
    1. Annotation and String based custom input variables
    1. Annotation and String based custom selection set
2. Static and dynamic HTTP request headers
3. Observability via Micrometer
4. Resilience with via Resilience4J
5. Flexible API allowing various pluggable HTTP clients
6. Asynchronous support
    1. CompletableFuture
    1. Pluggable asynchronous HTTP clients
    1. User provided executor services
7. Request Parameter Validation

## Quick start

Please read the **Quick Start** section in [Mocca documentation](docs/END_USER_DOCUMENT.md) for instructions on how to start using Mocca quickly.

## End-user documentation

Please refer to [Mocca documentation](docs/END_USER_DOCUMENT.md).

## Release notes
See [Mocca release notes](docs/RELEASE_NOTES.md).

## Reporting an issue
Please open an issue using our [GitHub issues](https://github.com/paypal/mocca/issues) page.

## Contributing
You are very welcome to contribute to Mocca! Read our [Contribution guidelines](docs/CONTRIBUTING.md).

## License
This project is licensed under the [MIT License](LICENSE.txt).