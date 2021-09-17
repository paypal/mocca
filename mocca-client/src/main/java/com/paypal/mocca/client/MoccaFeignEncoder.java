package com.paypal.mocca.client;

import com.paypal.mocca.client.MoccaSerializer.Variable;
import com.paypal.mocca.client.annotation.Mutation;
import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.SelectionSet;
import com.paypal.mocca.client.annotation.Var;
import com.paypal.mocca.client.annotation.RequestHeaderParam;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Mocca Feign encoder, responsible for serializing the request payload
 *
 * @author fabiocarvalho777@gmail.com
 */
class MoccaFeignEncoder implements Encoder {
    private static final Logger logger = LoggerFactory.getLogger(MoccaFeignEncoder.class);

    private final MoccaSerializer moccaSerializer = new MoccaSerializer();
    /**
     * Notice that this Validator is in the older javax.validation package not newer jakarta.validation package.
     * Please refer to {@link javax.validation.Validator} for more information.
     */
    private Validator validator;

    // The client is only used for validation and is not needed for encoding.
    private MoccaClient client;

    MoccaFeignEncoder() {
        try {
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            this.validator = validator;
        } catch (Exception e) {
            // No validation provider found
            logger.warn("No implementation of javax.validation.Validator was found on the classpath. Mocca will be unable to perform request parameters validation.");
        }
    }

    public void setClient(MoccaClient client) {
        this.client = client;
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {

        if (bodyType != Object[].class) {
            throw new MoccaException("Unexpected body object type: " + bodyType.getTypeName());
        }
        Object[] parameters = (Object[]) object;

        try {
            final Type responseType = template.methodMetadata().returnType();
            final String operationName = getOperationName(template);
            final OperationType operationType = getOperationType(template);
            final SelectionSet selectionSet = getSelectionSet(template);
            final List<Variable> variables = getVariables(parameters, template);
            if (validator != null) {
                validateVariables(parameters, template);
            }
            final byte[] data = moccaSerializer.serialize(variables, responseType, operationName, operationType, selectionSet);
            template.body(data, Charset.defaultCharset());

        } catch (IOException e) {
            throw new MoccaException("An error happened when serializing the request payload from type " + bodyType.getTypeName(), e);
        }
    }

    /**
     * Validates the client request using the bean validation
     * API for validating all the parameters in a method invocation.
     * The Feign request object points to the method being invoked but
     * not the client object itself. This is why we have to set the client
     * as a field in the encoder.
     * @param parameters all the parameters passed to the client method
     * @param template the Feign request template object
     */
    void validateVariables(Object[] parameters, RequestTemplate template) {

        Method method = template.methodMetadata().method();
        Set<ConstraintViolation<Object>> violationSet = validator.forExecutables().validateParameters(client, method, parameters);

        if (violationSet.size() > 0) {
            throw new EncodeException("Constraint violations found in one or more request parameters",
                    new ConstraintViolationException(violationSet)
            );
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
        return OperationType.valueOf(operationAnnotation);
    }

    private static Annotation getOperationAnnotation(RequestTemplate requestTemplate) {
        Method method = requestTemplate.methodMetadata().method();
        Query query = method.getAnnotation(Query.class);
        Mutation mutation = method.getAnnotation(Mutation.class);
        if (query == null && mutation == null) {
            throw new MoccaException("The operation method " + method.getName() + " is not annotated with " + Query.class.getName() + " nor " + Mutation.class.getName());
        }
        if (query != null && mutation != null) {
            throw new MoccaException("The operation method " + method.getName() + " is not annotated with both " + Query.class.getName() + " and " + Mutation.class.getName());
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
     * Returns a list containing the operation variables associated with a Feign request template object.
     * Operation variables are the operation method parameters annotated with {@link com.paypal.mocca.client.annotation.Var}.
     * If the request method doesn't have operation variables, an empty list is returned.
     * The list is ordered according to {@code parameters} order.
     *
     * @param parameters GraphQL operation method parameter values
     * @param requestTemplate the Feign request template object
     * @return a list containing the operation variables
     * associated with a Feign request template object
     */
    private static List<Variable> getVariables(Object[] parameters, RequestTemplate requestTemplate) {
        Parameter[] parametersMetadata = requestTemplate.methodMetadata().method().getParameters();
        List<Variable> variables = new ArrayList<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {

            // TODO We could make it configurable, leaving for the user to decide if null variables should
            // be omitted or just set with null value. For now we will just skip them.
            if (parameters[i] == null) {
                logger.info("Skipping parameter at position {} as it is set to null", i);
                continue;
            }

            Parameter parameterMetadata = parametersMetadata[i];
            Var varAnnotation = parameterMetadata.getAnnotation(Var.class);
            // FIXME It would be better if this check happened at client definition time, instead of request time
            if (varAnnotation == null) {
                if (parameterMetadata.getAnnotation(RequestHeaderParam.class) == null) {
                    final String method = requestTemplate.methodMetadata().method().getName();
                    throw new MoccaException("Invalid GraphQL operation method " + method + ", make sure all its parameters are annotated with one Mocca annotation");
                }
            } else {
                if (parameterMetadata.getAnnotation(RequestHeaderParam.class) != null) {
                    final String method = requestTemplate.methodMetadata().method().getName();
                    throw new MoccaException("Invalid GraphQL operation method " + method + ", make sure all its parameters are annotated with one Mocca annotation");
                }
                Variable variable = new Variable(parameters[i], parameterMetadata.getParameterizedType(), varAnnotation);
                variables.add(variable);
            }
        }
        return variables;
    }

}
