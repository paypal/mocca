package com.paypal.mocca.client;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Optional;

/**
 * Mocca utility class
 *
 * @author fabiocarvalho777@gmail.com
 */
final class MoccaUtils {

    private MoccaUtils() {
    }

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
            throw new IllegalArgumentException("Unsupported annotation " + operationAnnotation.getClass().getName());
        }

        String getValue() {
            return value;
        }
    }

    /*
     * Returns an optional with the Type parametrized inside the given type,
     * if the given type is parameterized and equals to the outer reference type.
     * If it is not, an empty optional is returned.
     */
    static Optional<Type> getInnerType(final Type type, final Class<?> outerTypeReference) {
        if (!(type instanceof ParameterizedType)) return Optional.empty();

        ParameterizedType parameterizedType = (ParameterizedType) type;
        if (parameterizedType.getRawType().getTypeName().equals(outerTypeReference.getName())) {
            return Optional.of(parameterizedType.getActualTypeArguments()[0]);
        } else {
            return Optional.empty();
        }
    }

    static Class<?> erase(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class) parameterizedType.getRawType();
        } else {
            Type[] typeVariableBounds;
            if (type instanceof TypeVariable) {
                TypeVariable typeVariable = (TypeVariable) type;
                typeVariableBounds = typeVariable.getBounds();
                return 0 < typeVariableBounds.length ? erase(typeVariableBounds[0]) : Object.class;
            } else if (type instanceof WildcardType) {
                WildcardType wildcardType = (WildcardType) type;
                typeVariableBounds = wildcardType.getUpperBounds();
                return 0 < typeVariableBounds.length ? erase(typeVariableBounds[0]) : Object.class;
            } else if (type instanceof GenericArrayType) {
                GenericArrayType genericArrayType = (GenericArrayType) type;
                return Array.newInstance(erase(genericArrayType.getGenericComponentType()), 0).getClass();
            } else {
                throw new IllegalArgumentException("Unknown Type kind: " + type.getClass());
            }
        }
    }

}