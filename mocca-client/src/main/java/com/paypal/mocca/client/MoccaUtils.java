package com.paypal.mocca.client;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
     * Returns an optional containing the Type parameterized inside the given type,
     * if the given type is parameterized and equals to the outer reference type.
     * If it is not, an empty optional is returned.
     */
    static Optional<Type> getInnerType(final Type type, final Type outerTypeReference) {
        if (!isParameterizedType(type)) return Optional.empty();

        ParameterizedType parameterizedType = (ParameterizedType) type;
        if (parameterizedType.getRawType().getTypeName().equals(outerTypeReference.getTypeName())) {
            return Optional.of(parameterizedType.getActualTypeArguments()[0]);
        } else {
            return Optional.empty();
        }
    }

    /*
     * Returns the first parametrized type inside the given type.
     * If the given type is not parameterized an IllegalArgumentException is thrown.
     */
    static Type getInnerType(final Type type) {
        if (!isParameterizedType(type)) throw new IllegalArgumentException("Given type is not parameterized");

        ParameterizedType parameterizedType = (ParameterizedType) type;
        return parameterizedType.getActualTypeArguments()[0];
    }

    /*
     * Returns true if the given type is a parameterized type.
     */
    static boolean isParameterizedType(Type type) {
        if (type == null) throw new IllegalArgumentException("Type cannot be null");

        return type instanceof ParameterizedType;
    }

    /*
     * Returns true if the given type is a parameterized type
     * and its raw type is the same as one of the given raw types
     */
    static boolean isParameterizedType(Type type, Type... rawTypes) {
        if (type == null) throw new IllegalArgumentException("Type cannot be null");
        if (rawTypes == null) throw new IllegalArgumentException("Raw type cannot be null");

        if(!(type instanceof ParameterizedType)) return false;
        Type actualRawType = ((ParameterizedType) type).getRawType();

        final Set<Type> rawTypesSet = new HashSet<>();
        for (Type t : rawTypes) rawTypesSet.add(t);
        return rawTypesSet.contains(actualRawType);
    }

    /*
     * Returns a class erased of any parameters, if there is any
     */
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