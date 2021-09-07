package com.paypal.mocca.client;

import java.lang.annotation.Annotation;

/**
 * Enumeration representing each supported GraphQL operation.
 * This enumeration is internal purposes only, bot being
 * part of Mocca API.
 *
 * @author fabiocarvalho777@gmail.com
 */
enum OperationType {

    Query("query"),
    Mutation("mutation");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }

    static OperationType getFromAnnotation(Annotation operationAnnotation) {
        if (operationAnnotation instanceof com.paypal.mocca.client.annotation.Query) return Query;
        if (operationAnnotation instanceof com.paypal.mocca.client.annotation.Mutation) return Mutation;
        throw new IllegalArgumentException("Unsupported annotation: " + operationAnnotation.getClass().getName());
    }

    String getValue() {
        return value;
    }
}