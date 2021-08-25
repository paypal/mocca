package com.paypal.mocca.client;

import com.paypal.mocca.client.MoccaUtils.OperationType;
import com.paypal.mocca.client.annotation.SelectionSet;
import com.paypal.mocca.client.annotation.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.paypal.mocca.client.MoccaUtils.erase;
import static com.paypal.mocca.client.MoccaUtils.getInnerType;
import static com.paypal.mocca.client.MoccaUtils.isParameterizedType;

/**
 * Mocca GraphQL request payload serializer
 *
 * @author fabiocarvalho777@gmail.com
 */
class MoccaSerializer {

    private static final Logger logger = LoggerFactory.getLogger(MoccaSerializer.class);

    MoccaSerializer() {
    }

    /**
     * The GraphQL operation variable, represented by its value,
     * type and annotation, containing metadata useful for the
     * encoder to write the message payload
     */
    static class Variable {
        private Object value;
        private Type type;
        private Var metadata;

        Variable(Object value, Type type, Var metadata) {
            this.value = value;
            this.type = type;
            this.metadata = metadata;
        }
    }

    /*
     * Serialize the given list of variables, using additional configuration parameters, and returns a byte array containing the GraphQL HTTP request payload
     *
     * @param variables list of GraphQL operation variable set in the operation method
     * @param responseType the return type set in the GraphQL operation method, useful when defining the request selection set
     * @param operationName the name of the GraphQL operation
     * @param operationType the type of the GraphQL operation
     * @param selectionSet the annotation used to specify the GraphQL selection set for this request
     * @return a byte array containing the GraphQL HTTP request payload
     * @throws IOException if any IO error happens when serializing the object
     */
    byte[] serialize(final List<Variable> variables, final Type responseType, final String operationName, final OperationType operationType, final SelectionSet selectionSet) throws IOException {
        ByteArrayOutputStream requestPayload = new ByteArrayOutputStream();

        // Adding beginning of payload all the way to input parameters
        write(requestPayload, "{\n  \"query\" : \"");
        write(requestPayload, operationType.getValue());
        write(requestPayload, "{");
        write(requestPayload, operationName);

        // Adding operation variables using object and its type
        writeRequestVariables(requestPayload, variables);

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
     * This method supports writing variables in two modes of operation. If it is a String, primitive or primitive wrapper,
     * then it is written as-is into the variables section of the request operation.
     * Now, if the variable type is a POJO, or a list of POJOs, then object is assumed to be a Java bean
     * and its properties (following Java beans standard) are automatically set as the operation variables in the GraphQL request.
     * If the POJO object has other POJOs as properties, their properties are also set as variables.
     *
     * @param requestPayload the output stream object used to write the request payload, based on the other parameters
     * @param variables list of GraphQL operation variable set in the operation method
     * @throws IOException if any IO error happens when writing the request variables
     */
    private void writeRequestVariables(final ByteArrayOutputStream requestPayload, final List<Variable> variables) throws IOException {
        if (variables.isEmpty()) {
            logger.debug("Variables list is empty, so no input parameter will be written to the GraphQL operation");
            return;
        }

        write(requestPayload, "(");

        boolean rawVariables = variables.size() == 1 && variables.get(0).metadata.raw();
        if (rawVariables) {
            final String rawVariablesValue = ((String) variables.get(0).value).replaceAll("\"", "\\\\\"");
            write(requestPayload, rawVariablesValue);
        } else {
            List<String> variableStrings = new ArrayList<>(variables.size());

            for (Variable variable : variables) {
                if (variable.metadata.raw()) {
                    // FIXME Class and method names are not available here. They should be added to the exception message though.
                    throw new MoccaException("Only one GraphQL operation method parameter can have `raw` set to true under annotation " + Var.class.getName());
                }

                String variableString;

                if (isParameterizedType(variable.type, List.class)) {
                    StringJoiner listVariablesJoiner = new StringJoiner(", ", variable.metadata.value() + ": [", "]");
                    List<?> variablesList = (List<?>) variable.value;
                    if (!variablesList.isEmpty()) {
                        final Type listType = getInnerType(variable.type);
                        if (isPojo(listType)) {
                            final List<String> ignoreFields = variable.metadata != null ? Arrays.asList(variable.metadata.ignore()) : Collections.emptyList();
                            variablesList.forEach(v -> {
                                ByteArrayOutputStream requestDtoOutputStream = new ByteArrayOutputStream();
                                writeRequestPojo(requestDtoOutputStream, null, listType, v, ignoreFields);
                                listVariablesJoiner.add(requestDtoOutputStream.toString());
                            });
                        } else {
                            variablesList.forEach(v -> listVariablesJoiner.add(writeRequestVariable(null, v, listType)));
                        }
                    }
                    variableString = listVariablesJoiner.toString();
                } else {
                    final Type type;
                    final Object value;

                    if (isParameterizedType(variable.type, Optional.class)) {
                        Optional valueOptional = (Optional) variable.value;
                        if (!valueOptional.isPresent()) {
                            logger.warn("Skipping empty Optional variable {}", variable.metadata.value());
                            continue;
                        }
                        type = getInnerType(variable.type);
                        value = valueOptional.get();
                    } else {
                        type = variable.type;
                        value = variable.value;
                    }

                    if (isPojo(type)) {
                        List<String> ignoreFields = variable.metadata != null ? Arrays.asList(variable.metadata.ignore()) : Collections.emptyList();
                        ByteArrayOutputStream requestDtoOutputStream = new ByteArrayOutputStream();
                        writeRequestPojo(requestDtoOutputStream, variable.metadata.value(), type, value, ignoreFields);
                        variableString = requestDtoOutputStream.toString();
                    } else {
                        variableString = writeRequestVariable(variable.metadata.value(), value, type);
                    }
                }

                variableStrings.add(variableString);
            }

            write(requestPayload, String.join(", ", variableStrings));
        }
        write(requestPayload, ")");
    }

    private static final Set<Type> NON_POJO_TYPES = new HashSet<>();
    static {
        NON_POJO_TYPES.add(Optional.class);
        NON_POJO_TYPES.add(OffsetDateTime.class);
        NON_POJO_TYPES.add(Character.class);
        NON_POJO_TYPES.add(String.class);
        NON_POJO_TYPES.add(Boolean.class);
    }

    /*
     * Checks if the type is a POJO, which is anything different
     * than a primitive, a primitive wrapper, or String.
     * There are other special types that are not considered
     * POJOs, such as java.time.OffsetDateTime and java.util.Optional.
     *
     * @param type the type to be evaluated
     * @return whether the type is complex
     */
    private static boolean isPojo(Type type) {
        if (NON_POJO_TYPES.contains(type)) return false;
        if (!(type instanceof Class)) return false;
        Class clazz = (Class) type;
        if (Number.class.isAssignableFrom(clazz)) return false;
        return !clazz.isPrimitive();
    }

    /*
     * Returns a String containing the specification of a GraphQL variable name and its value,
     * as it is supposed to be written in the request payload
     */
    private String writeRequestVariable(String name, Object value, Type type) {
        final String prefix = name == null ? "" : name + ": ";
        return type == String.class || type == Character.class || type == OffsetDateTime.class || type.getTypeName().equals("char") ?
                prefix + "\\\"" + value.toString() + "\\\"" :
                prefix + value.toString();
    }

    /*
     * Writes to the given output stream a GraphQL variable using the given POJO
     * and its properties (following Java beans standard). If the POJO has other POJOs as properties,
     * their properties are also set as variables.
     *
     * @param outputStream the stream the GraphQL variable should be written into
     * @param valueName the name of the value to be written (which can be a GraphQL variable, or a field inside a complex type)
     * @param valueType the type of the value used to be written as part of the GraphQL variables
     * @param value the value to be written as part of the GraphQL variables
     * @param ignoreFields the names of properties present in the given value, but to be skipped when writing the GraphQL operation variables
     *        (names of properties in inner POJOs are specified using the outer field name followed by dot)
     */
    private void writeRequestPojo(final ByteArrayOutputStream outputStream, final String valueName, final Type valueType, final Object value, final List<String> ignoreFields) {
        try {
            if (valueName != null) {
                write(outputStream, valueName);
                write(outputStream, ": ");
            }
            write(outputStream, "{");
            writeRequestPojo(outputStream, valueType, value, ignoreFields);
            write(outputStream, "}");
        } catch (Exception e) {
            throw new MoccaException("An error happened when writing request variable object of type " + valueType, e);
        }
    }

    /*
     * Writes to the given output stream a GraphQL variable using the given POJO
     * and its properties (following Java beans standard). If the POJO has other POJOs as properties,
     * their properties are also set as variables (notice in this case this method is called recursively).
     *
     * @param outputStream the stream the GraphQL variable should be written into
     * @param valueType the type of the value used to be written as part of the GraphQL variables
     * @param value the value to be written as part of the GraphQL variables
     * @param ignoreFields the names of properties present in the given value, but to be skipped when writing the GraphQL operation variables
     *        (names of properties in inner POJOs are specified using the outer field name followed by dot)
     */
    private void writeRequestPojo(final ByteArrayOutputStream outputStream, final Type valueType, final Object value, final List<String> ignoreFields) {
        try {
            final BeanInfo info = Introspector.getBeanInfo(erase(valueType));
            final PropertyDescriptor[] pds = info.getPropertyDescriptors();
            final List<String> variables = Arrays.stream(pds)
                    .filter(pd -> !pd.getName().equals("class"))
                    .filter(pd -> !ignoreFields.contains(pd.getName()))
                    .map(pd -> {
                        try {
                            return new Tuple<>(pd.getName(), pd.getReadMethod().invoke(value));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            logger.warn("Request DTO property " + pd.getName() + " could not be accessed", e);
                            return new Tuple<>(pd.getName(), null);
                        }
                    })
                    .filter(e -> e.value != null)
                    .peek(e -> {
                        Object v = e.value;
                        e.value = objectToString(v, e.key, ignoreFields);
                    })
                    .map(e -> e.key + ": " + e.value)
                    .collect(Collectors.toList());

            write(outputStream, String.join(", ", variables));
        } catch (Exception e) {
            throw new MoccaException("An error happened when writing request DTO object of type " + valueType, e);
        }
    }

    /**
     * Returns the String representation (in GraphQL variable notation) of the given object
     *
     * @param object the object whose String representation should be returned
     * @param name the name of the GraphQL variable
     * @param ignoreFields the object fields to be ignored
     * @return the String representation (in GraphQL variable notation) of the given object
     */
    private String objectToString(final Object object, final String name, final List<String> ignoreFields) {
        if (object instanceof String || object instanceof OffsetDateTime) {
            return "\\\"" + object + "\\\"";
        } else if (object instanceof Number || object instanceof Boolean) {
            return String.valueOf(object);
        } else if (object instanceof List) {
            List<?> listElement = (List) object;
            List<String> stringElements = listElement.stream().
                    map(le -> objectToString(le, name, ignoreFields)).
                    collect(Collectors.toList());
            return "[" + String.join(", ", stringElements) + "]";
        } else {
            ByteArrayOutputStream complexVariable = new ByteArrayOutputStream();

            writeRequestPojo(complexVariable, object.getClass(), object,
                    getNextIgnoreFields(name + ".", ignoreFields));
            return "{" + complexVariable.toString() + "}";
        }
    }

    /**
     * Remove the current key name from the ignore fields to get the ignore names
     * for objects lower down in the hierarchy
     * @param prefix prefix to remove
     * @param ignoreFields current list of ignore fields
     * @return new list of ignore fields for objects at or below the current position
     */
    private List<String> getNextIgnoreFields(String prefix, final List<String> ignoreFields) {
        List<String> specificIgnoreFields = ignoreFields.stream()
                .filter(f -> f.startsWith(prefix))
                .map(f -> f.substring(prefix.length()))
                .collect(Collectors.toList());
        return specificIgnoreFields;
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
     * @throws MoccaException if a cycle is found or any error happens when writing the selection set
     */
    private void writeSelectionSet(final ByteArrayOutputStream requestPayload, final Type responseType) {

        // This is necessary to detect cycles and prevent stack overflow
        Set<Type> seenPojoTypes = new HashSet<>();

        writeSelectionSet(requestPayload, responseType, seenPojoTypes);
    }

    /*
     * Writes the selection set of the GraphQL request message using the response type as reference.
     * Notice this process also supports field names defined in inner types (done recursively).
     *
     * @param requestPayload the output stream object used to write the selection set, based on the other parameters
     * @param responseType the return type set in the GraphQL operation method, necessary to dynamically set the selection set
     * @param seenPojoTypes to detect cycles and prevent stack overflow
     * @throws MoccaException if a cycle is found or any error happens when writing the selection set
     */
    private void writeSelectionSet(final ByteArrayOutputStream requestPayload, final Type responseType, Set<Type> seenPojoTypes) throws MoccaException {
        try {

            // Retrieving type out of parameterized types if necessary
            final Type cfResponseType = getInnerType(responseType, CompletableFuture.class).orElse(responseType);
            final Type listResponseType = getInnerType(cfResponseType, List.class).orElse(cfResponseType);
            final Type rawResponseType = getInnerType(listResponseType, Optional.class).orElse(listResponseType);

            if (!isPojo(rawResponseType)) {
                // Nothing to be done here.
                // A selection set should not exist if the return type is not a POJO.
                return;
            }

            if (seenPojoTypes.contains(rawResponseType)) {
                throw new MoccaException("Selection set cannot be specified as there is a cycle in the return type caused by " + rawResponseType);
            }
            seenPojoTypes.add(rawResponseType);

            write(requestPayload, " {");
            BeanInfo info = Introspector.getBeanInfo(erase(rawResponseType));
            PropertyDescriptor[] pds = info.getPropertyDescriptors();
            List<String> selectionSet = Arrays.stream(pds)
                    .filter(pd -> !pd.getReadMethod().getReturnType().equals(Class.class))
                    // Parameterized types should not be treated as Java Beans and
                    // shouldn't be serialized. The exception though is Lists
                    // and Optionals, which should have their parameter types
                    // processed instead.
                    .filter(pd -> {
                        Type type = pd.getReadMethod().getGenericReturnType();
                        if (!isParameterizedType(type)) return true;
                        return isParameterizedType(type, List.class, Optional.class);
                    })
                    .map(pd -> new Tuple<>(pd.getName(), pd.getReadMethod().getGenericReturnType()))
                    .map(e -> {
                        Type type = e.value;
                        if (isPojo(type)) {
                            return writeSelectionSetPojo(e.key, type, seenPojoTypes);
                        } else if (isParameterizedType(type)) {
                            // Here we know it is either an Optional or a List
                            final Type typeParameter = getInnerType(type);
                            if (isPojo(typeParameter)) return writeSelectionSetPojo(e.key, typeParameter, seenPojoTypes);
                        }
                        // If this line is reached, that means this is not a POJO and serialization
                        // (for this particular field at least) in the selection set should end here.
                        return e.key;
                    })
                    .collect(Collectors.toList());
            write(requestPayload, String.join(" ", selectionSet));
            write(requestPayload, "}");

            seenPojoTypes.remove(rawResponseType);
        } catch (Exception e) {
            throw new MoccaException("An error happened when writing selection set of type " + responseType, e);
        }
    }

    /**
     * Returns the String representation of a POJO in GraphQL SelectionSet notation
     *
     * @param fieldName the element (GraphQL field name and type) whose String representation should be returned
     * @param type the particular type to be used to create the String representation of the given element
     * @return the String representation of a POJO in GraphQL SelectionSet notation
     */
    private String writeSelectionSetPojo(final String fieldName, final Type type, Set<Type> seenPojoTypes) {
        ByteArrayOutputStream complexVariable = new ByteArrayOutputStream();
        writeSelectionSet(complexVariable, type, seenPojoTypes);
        return fieldName + complexVariable.toString();
    }

    /**
     * Utility method to write the given data into the given output stream
     *
     * @param outputStream the output stream to be written to
     * @param data the data to be written
     * @throws IOException in case of any IOException during the write operation
     */
    private void write(ByteArrayOutputStream outputStream, final String data) throws IOException {
        outputStream.write(data.getBytes());
    }

}