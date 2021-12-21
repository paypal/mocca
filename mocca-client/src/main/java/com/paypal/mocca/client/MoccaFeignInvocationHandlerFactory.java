package com.paypal.mocca.client;

import feign.FeignException;
import feign.InvocationHandlerFactory;
import feign.Target;

import javax.validation.ConstraintViolationException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class allows Mocca to hide Feign exception types from applications
 *
 * @author crankydillo@gmail.com, facarvalho
 */
class MoccaFeignInvocationHandlerFactory implements InvocationHandlerFactory {

    private final InvocationHandlerFactory delegate = new InvocationHandlerFactory.Default();

    private static final Set<Class<? extends Throwable>> acceptableExceptions = new HashSet<>();

    static {
        acceptableExceptions.add(MoccaException.class);
        acceptableExceptions.add(ConstraintViolationException.class);
    }

    @Override
    public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
        return (proxy, method, args) -> {
            try {
                return delegate.create(target, dispatch).invoke(proxy, method, args);
            } catch (FeignException e) {
                Throwable cause = e.getCause();
                if (acceptableExceptions.contains(cause.getClass())) {
                    throw cause;
                } else {
                    throw new MoccaException("A exception has happened when invoking the client target", e);
                }
            }
        };
    }

}