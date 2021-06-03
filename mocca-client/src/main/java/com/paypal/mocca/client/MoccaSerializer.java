package com.paypal.mocca.client;

import com.paypal.mocca.client.MoccaUtils.OperationType;
import com.paypal.mocca.client.annotation.SelectionSet;
import com.paypal.mocca.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Mocca GraphQL request payload serializer
 *
 * @author fabiocarvalho777@gmail.com
 */
class MoccaSerializer {

    private static final Logger logger = LoggerFactory.getLogger(MoccaSerializer.class);

    MoccaSerializer() {
    }

    /*
     * Serialize the given payload object and, using additional configuration parameters, returns a byte array containing the GraphQL HTTP request payload
     *
     * @param object this can be a request DTO object or a String (if requestType is String.class) containing the operation variables
     * @param requestType the type of the object used to define the operation variables
     * @param responseType the return type set in the GraphQL operation method, useful when defining the request selection set
     * @param operationName the name of the GraphQL operation
     * @param operationType the type of the GraphQL operation
     * @param variable the variable annotation defined by the user in the GraphQL operation (this can be null)
     * @return a byte array containing the GraphQL HTTP request payload
     * @throws IOException if any IO error happens when serializing the object
     */
    byte[] serialize(final Object object, final Type requestType, final Type responseType, final String operationName, final OperationType operationType, final SelectionSet selectionSet, final Variable variable) throws IOException {
        ByteArrayOutputStream requestPayload = new ByteArrayOutputStream();

        // Adding beginning of payload all the way to input parameters
        write(requestPayload, "{\n  \"query\" : \"");
        write(requestPayload, operationType.getValue());
        write(requestPayload, "{");
        write(requestPayload, operationName);

        // Adding operation variables using object and its type
        writeRequestVariables(requestPayload, object, requestType, variable);

        if (selectionSet != null && (selectionSet.value().equals(SelectionSet.UNDEFINED) || selectionSet.value().trim().isEmpty())) {
            throw new MoccaException("A com.paypal.mocca.client.annotation.SelectionSet annotation with an undefined value is present at the method related to operation "
                    + operationName + ". Please, set its value, or remove the annotation, letting Mocca use the return type to automatically set the selection set.");
        } else if (selectionSet != null) {
            // Adding selection set using the selection set annotation
            writeSelectionSet(requestPayload, selectionSet);
        } else if (responseType != null) {
            // Adding selection set using the response type
            writeSelectionSet(requestPayload, responseType);
        } else {
            logger.debug("Response type is null and a custom selection set was not provided, so a selection set will not be written to the GraphQL operation");
        }

        // Adding end of payload right after selection set
        write(requestPayload, "}\"\n}");

        return requestPayload.toByteArray();
    }

    /*
     * This class represents a key value pair object and is useful
     * when processing properties in a request DTO object or response type
     *
     * @param <T> the type used in the value
     */
    private static class Tuple<T> {
        String key;
        T value;
        Tuple(String key, T value) {
            this.key = key;
            this.value = value;
        }
    }

    /*
     * Writes the variables section of the request payload according to the given parameters.
     * This method supports two modes of operation. If the given request type is String.class,
     * then object (which should be a String) is written as-is into the variables section of the request operation.
     * Now, if the given request type is NOT String.class, then object is assumed to be a DTO object (based on the given type)
     * and its properties (following Java beans standard) are automatically set as the operation variables in the GraphQL request.
     * If the DTO object has other DTOs as properties, their properties are also set as variables.
     *
     * @param requestPayload the output stream object used to write the request payload, based on the other parameters
     * @param object this can be a request DTO object or a String (if requestType is String.class) containing the operation variables
     * @param requestType the type of the object used to define the operation variables
     * @param variable an optional possibly containing the variable annotation defined by the user in the GraphQL operation (this can be null)
     * @throws IOException if any IO error happens when writing the request variables
     */
    private void writeRequestVariables(final ByteArrayOutputStream requestPayload, final Object object, final Type requestType, Variable variable) throws IOException {
        if (object == null || object instanceof String && ((String) object).trim().isEmpty()) {
            logger.debug("Request DTO object is null, so no input parameter will be written to the GraphQL operation");
            return;
        }

        write(requestPayload, "(");

        if (requestType.equals(String.class)) {
            String requestVariables = ((String) object).replaceAll("\"", "\\\\\"");
            write(requestPayload, requestVariables);
        } else {
            List<String> ignoreFields = variable != null ? Arrays.asList(variable.ignore()) : Collections.emptyList();
            writeRequestDto(requestPayload, object, requestType, ignoreFields);
        }

        write(requestPayload, ")");
    }

    /*
     * Writes to the given output stream the variables section of the request payload using a given DTO object
     * and its properties (following Java beans standard). If the DTO object has other DTOs as properties,
     * their properties are also set as variables (notice in this case this method is called recursively).
     *
     * @param requestPayload the output stream object used to write the request payload, based on the other parameters
     * @param object the request DTO object containing the operation variables
     * @param requestType the type of the object used to define the operation variables
     * @param ignoreFields name of properties present in the given object, but to be skipped when writing the GraphQL operation variables
     *        (names of properties in inner DTOs are specified using the outer field name followed by dot)
     */
    // TODO Add a parameter here (to be exposed in the Variable annotation as user configuration)
    //  to limit how deep in the request DTO this process should go (to avoid cycles).
    //  If not set by the user, a default number should be set (10 for example).
    private void writeRequestDto(final ByteArrayOutputStream requestPayload, final Object object, final Type requestType, final List<String> ignoreFields) {
        try {
            final BeanInfo info = Introspector.getBeanInfo(MoccaUtils.erase(requestType));
            final PropertyDescriptor[] pds = info.getPropertyDescriptors();
            final List<String> variables = Arrays.stream(pds)
                    .filter(pd -> !pd.getName().equals("class"))
                    .filter(pd -> !ignoreFields.contains(pd.getName()))
                    .map(pd -> {
                        try {
                            return new Tuple<>(pd.getName(), pd.getReadMethod().invoke(object));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            logger.warn("Request DTO property " + pd.getName() + " could not be accessed", e);
                            return new Tuple<>(pd.getName(), null);
                        }
                    })
                    .filter(e -> e.value != null)
                    .peek(e -> {
                        Object v = e.value;
                        if (v instanceof String) {
                            e.value = "\\\"" + v + "\\\"";
                        } else if (v instanceof Number || v instanceof Boolean) {
                            e.value = String.valueOf(v);
                        } else {
                            ByteArrayOutputStream complexVariable = new ByteArrayOutputStream();

                            final String prefix = e.key + ".";
                            List<String> specificIgnoreFields = ignoreFields.stream()
                                    .filter(f -> f.startsWith(prefix))
                                    .map(f -> f.substring(prefix.length()))
                                    .collect(Collectors.toList());

                            writeRequestDto(complexVariable, v, v.getClass(), specificIgnoreFields);
                            e.value = "{" + complexVariable.toString() + "}";
                        }
                    })
                    .map(e -> e.key + ": " + e.value)
                    .collect(Collectors.toList());

            write(requestPayload, String.join(", ", variables));
        } catch (Exception e) {
            throw new MoccaException("An error happened when writing request DTO object of type " + requestType, e);
        }
    }

    /*
     * Writes the selection set of the GraphQL request message using the user provided SelectionSet annotation as reference.
     *
     * @param requestPayload the output stream object used to write the selection set, based on the other parameters
     * @param selectionSet the SelectionSet annotation set in the GraphQL operation method, necessary to set the selection set
     */
    private void writeSelectionSet(final ByteArrayOutputStream requestPayload, final SelectionSet selectionSet) {
        try {
            String selectionSetValue = selectionSet.value();
            if (selectionSetValue.trim().isEmpty()) logger.warn("Annotation provided selection set is blank");

            // TODO In the future selection set validation could be added here, saving a remote call to then have it validated on the server side.
            //  Also, it would be even better if this validation happened at client build time (since it is static), as opposed to request time.

            write(requestPayload, " ");
            write(requestPayload, selectionSetValue);
        } catch (Exception e) {
            throw new MoccaException("An error happened when writing annotation provided selection set: " + selectionSet.value(), e);
        }
    }

    /*
     * Writes the selection set of the GraphQL request message using the response type as reference.
     * Notice this process also supports field names defined in inner types (done recursively).
     *
     * @param requestPayload the output stream object used to write the selection set, based on the other parameters
     * @param responseType the return type set in the GraphQL operation method, necessary to dynamically set the selection set
     */
    // TODO Add a parameter here (to be exposed in the SelectionSet annotation as user configuration)
    //  to limit how deep in the response type this process should go (to avoid cycles).
    //  If not set by the user, a default number should be set (10 for example).
    private void writeSelectionSet(final ByteArrayOutputStream requestPayload, final Type responseType) {
        try {
            write(requestPayload, " {");

            // Retrieving DTO type out of completable future if necessary
            final Type cfResponseDtoType = MoccaUtils.getInnerType(responseType, CompletableFuture.class).orElse(responseType);

            // Retrieving DTO type out of list if necessary
            final Type responseDtoType = MoccaUtils.getInnerType(cfResponseDtoType, List.class).orElse(cfResponseDtoType);

            BeanInfo info = Introspector.getBeanInfo(MoccaUtils.erase(responseDtoType));
            PropertyDescriptor[] pds = info.getPropertyDescriptors();
            List<String> selectionSet = Arrays.stream(pds)
                    .filter(pd -> !pd.getName().equals("class"))
                    .map(pd -> new Tuple<Class<?>>(pd.getName(), pd.getReadMethod().getReturnType()))
                    .map(e -> {
                        Class<?> c = e.value;
                        if (c == String.class || c == Integer.class || c == Float.class || c == Boolean.class
                                || c.getName().equals("int") || c.getName().equals("float") || c.getName().equals("boolean")) {
                            return e.key;
                        } else {
                            ByteArrayOutputStream complexVariable = new ByteArrayOutputStream();
                            writeSelectionSet(complexVariable, c);
                            return e.key + complexVariable.toString();
                        }
                    })
                    .collect(Collectors.toList());
            write(requestPayload, String.join(" ", selectionSet));
            write(requestPayload, "}");
        } catch (Exception e) {
            throw new MoccaException("An error happened when writing selection set of type " + responseType, e);
        }
    }

    private void write(ByteArrayOutputStream requestPayload, final String data) throws IOException {
        requestPayload.write(data.getBytes());
    }

}