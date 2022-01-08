package com.paypal.mocca.client;

import feign.FeignException;
import feign.InvocationHandlerFactory;
import feign.Target;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * This class allows Mocca to hide Feign exception types from applications
 * when using a custom Feign invocation handler factory is possible
 *
 * @author facarvalho, crankydillo@gmail.com
 */
class MoccaFeignInvocationHandlerFactory implements InvocationHandlerFactory {

    private final InvocationHandlerFactory delegate = new InvocationHandlerFactory.Default();

    @Override
    public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
        return (proxy, method, args) -> {
            try {
                return delegate.create(target, dispatch).invoke(proxy, method, args);
            } catch (FeignException e) {
                throw MoccaExceptionHandler.handleException(e);
            }
        };
    }

}