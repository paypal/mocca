package com.paypal.mocca.client;

import feign.FeignException;
import feign.InvocationHandlerFactory;
import feign.Target;
import io.github.resilience4j.feign.FeignDecorator;
import io.github.resilience4j.feign.FeignDecorators;
import io.vavr.CheckedFunction1;

import java.lang.reflect.Method;

/**
 * This class allows Mocca to hide Feign exception types from applications
 * when using Resilience4j
 *
 * @author facarvalho, crankydillo@gmail.com
 * @author facarvalho
 */
class MoccaFeignDecorators implements FeignDecorator {

    private FeignDecorators feignDecorators;

    MoccaFeignDecorators(FeignDecorators feignDecorators) {
        this.feignDecorators = feignDecorators;
    }

    @Override
    public CheckedFunction1<Object[], Object> decorate(CheckedFunction1<Object[], Object> invocationCall, Method method, InvocationHandlerFactory.MethodHandler methodHandler, Target<?> target) {
        return (clientMethodParameters) -> {
            try {
                return feignDecorators
                        .decorate(invocationCall, method, methodHandler, target)
                        .apply(clientMethodParameters);
            } catch (FeignException e) {
                throw MoccaExceptionHandler.handleException(e);
            }
        };
    }

}