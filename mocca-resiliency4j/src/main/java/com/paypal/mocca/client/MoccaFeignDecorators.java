package com.paypal.mocca.client;

import feign.FeignException;
import feign.InvocationHandlerFactory;
import feign.Target;
import io.github.resilience4j.feign.FeignDecorator;
import io.github.resilience4j.feign.FeignDecorators;
import io.vavr.CheckedFunction1;

import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author facarvalho
 */
class MoccaFeignDecorators implements FeignDecorator {

    private FeignDecorators feignDecorators;
    private static final Set<Class<? extends Throwable>> acceptableExceptions = new HashSet<>();

    static {
        acceptableExceptions.add(MoccaException.class);
        acceptableExceptions.add(ConstraintViolationException.class);
    }

    MoccaFeignDecorators(FeignDecorators feignDecorators) {
        this.feignDecorators = feignDecorators;
    }

    @Override
    public CheckedFunction1<Object[], Object> decorate(CheckedFunction1<Object[], Object> invocationCall, Method method, InvocationHandlerFactory.MethodHandler methodHandler, Target<?> target) {
        return (obj) -> {
            try {
                CheckedFunction1<Object[], Object> decorator = feignDecorators.decorate(invocationCall, method, methodHandler, target);
                return decorator.apply(obj);
            } catch (FeignException e) {
                Throwable cause = e.getCause();
                if (acceptableExceptions.contains(cause.getClass()) && cause instanceof RuntimeException) {
                    throw cause;
                } else {
                    throw new MoccaException("A exception has happened when invoking the client target", e);
                }
            }
        };
    }

}