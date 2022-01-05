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
        // Only runtime exceptions can be added here
        acceptableExceptions.add(MoccaException.class);
        acceptableExceptions.add(ConstraintViolationException.class);
    }

    /**
     * If an {@link Error} is provided as parameter, that will be returned.
     * If a checked exception is provided as parameter, that will be returned.
     * If a {@link FeignException} is provided as parameter containing as cause
     * one of the types in {@code acceptableExceptions}, then the Feign exception
     * will be discarded, and its cause will be returned.
     * If any other {@link RuntimeException} is provided as parameter, that will be returned
     * wrapped in a {@link MoccaException}
     *
     * @param throwable a throwable to handled by Mocca
     * @return a throwable that maybe the same given as parameter or not
     */
    static Throwable handleException(Throwable throwable) {
        if (throwable instanceof FeignException) {
            Throwable cause = throwable.getCause();
            if (cause != null && acceptableExceptions.contains(cause.getClass())) {
                return cause;
            }
        }
        if (throwable instanceof RuntimeException) {
            return new MoccaException("The invocation of a client method has resulted in an exception: ", throwable);
        }
        return throwable;
    }

}