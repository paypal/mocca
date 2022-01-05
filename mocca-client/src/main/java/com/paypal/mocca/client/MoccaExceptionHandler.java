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
final class MoccaExceptionHandler {

    private MoccaExceptionHandler() {
    }

    private static final Set<Class<? extends Throwable>> acceptableExceptions = new HashSet<>();

    static {
        acceptableExceptions.add(MoccaException.class);
        acceptableExceptions.add(ConstraintViolationException.class);
    }

    static Throwable handleException(Throwable throwable) {
        if (throwable instanceof FeignException) {
            Throwable cause = throwable.getCause();
            if (cause != null && acceptableExceptions.contains(cause.getClass())) {
                return cause;
            }
        } else if (throwable instanceof RuntimeException) {
            return new MoccaException("The invocation of a client method has resulted in an exception: ", throwable);
        }
        return throwable;
    }

}