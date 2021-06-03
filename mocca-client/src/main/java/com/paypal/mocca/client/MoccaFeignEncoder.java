package com.paypal.mocca.client;

import com.paypal.mocca.client.MoccaUtils.OperationType;
import com.paypal.mocca.client.annotation.Mutation;
import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.RequestHeaderParam;
import com.paypal.mocca.client.annotation.SelectionSet;
import com.paypal.mocca.client.annotation.Variable;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.EncodeException;
import feign.codec.Encoder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Mocca Feign encoder, responsible for serializing the request payload
 *
 * @author fabiocarvalho777@gmail.com
 */
class MoccaFeignEncoder implements Encoder {

    private final MoccaSerializer moccaSerializer = new MoccaSerializer();

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {

        try {
            final Type responseType = template.methodMetadata().returnType();
            final String operationName = getOperationName(template);
            final OperationType operationType = getOperationType(template);
            final SelectionSet selectionSet = getSelectionSet(template);
            final Optional<Variable> variable = getVariable(template);

            final byte[] data = moccaSerializer.serialize(object, bodyType, responseType, operationName, operationType, selectionSet, variable.orElse(null));
            template.body(data, Charset.defaultCharset());

        } catch (IOException e) {
            throw new MoccaException("An error happened when serializing the request payload from type " + bodyType.getTypeName(), e);
        }
    }

    /**
     * Returns the operation name associated with a Feign response object
     *
     * @param response the Feign response object
     * @return the operation name associated with a Feign response object
     */
    static String getOperationName(Response response) {
        return getOperationName(response.request().requestTemplate());
    }

    /**
     * Returns the operation name associated with a Feign request template object
     *
     * @param requestTemplate the Feign request template object
     * @return the operation name associated with a Feign request template object
     */
    static String getOperationName(RequestTemplate requestTemplate) {
        final String methodName = requestTemplate.methodMetadata().method().getName();
        final Annotation operationAnnotation = getOperationAnnotation(requestTemplate);
        final String operationName;
        if (operationAnnotation instanceof Query) {
            Query annotation = (Query) operationAnnotation;
            operationName = annotation.name().equals(Query.UNDEFINED) ? methodName : annotation.name();
        } else if (operationAnnotation instanceof Mutation) {
            Mutation annotation = (Mutation) operationAnnotation;
            operationName = annotation.name().equals(Mutation.UNDEFINED) ? methodName : annotation.name();
        } else {
            throw new IllegalStateException("The operation method " + methodName + " is not annotated with an unsupported operation annotation " + operationAnnotation.getClass().getName());
        }
        return operationName;
    }

    /**
     * Returns the operation type associated with a Feign request template object
     *
     * @param requestTemplate the Feign request template object
     * @return the operation type associated with a Feign request template object
     */
    static OperationType getOperationType(RequestTemplate requestTemplate) {
        Annotation operationAnnotation = getOperationAnnotation(requestTemplate);
        return OperationType.getFromAnnotation(operationAnnotation);
    }

    private static Annotation getOperationAnnotation(RequestTemplate requestTemplate) {
        Method method = requestTemplate.methodMetadata().method();
        Query query = method.getAnnotation(Query.class);
        Mutation mutation = method.getAnnotation(Mutation.class);
        if (query == null && mutation == null) {
            throw new IllegalStateException("The operation method " + method.getName() + " is not annotated with " + Query.class.getName() + " nor " + Mutation.class);
        }
        if (query != null && mutation != null) {
            throw new IllegalStateException("The operation method " + method.getName() + " is not annotated with both " + Query.class.getName() + " and " + Mutation.class);
        }
        return query != null ? query : mutation;
    }

    /**
     * Returns the selection set associated with a Feign request template object
     *
     * @param requestTemplate the Feign request template object
     * @return the selection set associated with a Feign request template object
     */
    static SelectionSet getSelectionSet(RequestTemplate requestTemplate) {
        Method method = requestTemplate.methodMetadata().method();
        return method.getAnnotation(SelectionSet.class);
    }

    /**
     * Returns an optional containing the operation variable annotation
     * associated with a Feign request template object.
     * If the request method doesn't have a variable annotation,
     * an empty optional is returned.
     *
     * @param requestTemplate the Feign request template object
     * @return an optional containing the operation variable annotation
     * associated with a Feign request template object
     */
    static Optional<Variable> getVariable(RequestTemplate requestTemplate) {
        Method method = requestTemplate.methodMetadata().method();
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return Optional.empty();
        }
        int varCount = 0;
        int varIndex = 0;
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getAnnotation(RequestHeaderParam.class) == null) {
                varCount++;
                varIndex = i;
            }
        }
        if (varCount > 1) {
            throw new IllegalStateException("The operation method " + method.getName() + " has more than one parameter " +
                    "not annotated with RequestHeaderParam annotation");
        }
        return Optional.ofNullable(parameters[varIndex].getAnnotation(Variable.class));
    }
}
