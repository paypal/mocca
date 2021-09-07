package com.paypal.mocca.client;

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
 * This class provide reflection utility static methods
 * used across Mocca project
 *
 * @author fabiocarvalho777@gmail.com
 */
final class MoccaReflection {

    private MoccaReflection() {
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
        Arguments.require(isParameterizedType(type), "Given type is not parameterized");

        ParameterizedType parameterizedType = (ParameterizedType) type;
        return parameterizedType.getActualTypeArguments()[0];
    }

    /*
     * Returns true if the given type is a parameterized type.
     */
    static boolean isParameterizedType(Type type) {
        Arguments.requireNonNull(type, "Type cannot be null");

        return type instanceof ParameterizedType;
    }

    /*
     * Returns true if the given type is a parameterized type
     * and its raw type is the same as one of the given raw types
     */
    static boolean isParameterizedType(Type type, Type... rawTypes) {
        Arguments.requireNonNull(type, "Type cannot be null");
        Arguments.requireNonNull(rawTypes, "Raw type cannot be null");

        if(!(type instanceof ParameterizedType)) return false;
        final Type actualRawType = ((ParameterizedType) type).getRawType();

        for (Type t : rawTypes) if (t == actualRawType) return true;
        return false;
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