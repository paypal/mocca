package com.paypal.mocca.client;

import feign.FeignException;

import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Set;

/**
 * This exception handler allows Mocca to hide Feign exception types from applications
 *
 * @author facarvalho
 */
class MoccaExceptionHandler<T, R> {

    private static final Set<Class<? extends Throwable>> acceptableExceptions = new HashSet<>();

    static {
        acceptableExceptions.add(MoccaException.class);
        acceptableExceptions.add(ConstraintViolationException.class);
    }

    static RuntimeException handleException(Throwable throwable) {
        if (throwable instanceof FeignException) {
            Throwable cause = throwable.getCause();
            if (acceptableExceptions.contains(cause.getClass()) && cause instanceof RuntimeException) {
                return (RuntimeException) cause;
            }
        }
        return new MoccaException("An exception has happened when invoking the client target", throwable);
    }

}