package com.paypal.mocca.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Mocca GraphQL response payload deserializer
 *
 * @author fabiocarvalho777@gmail.com
 */
class MoccaDeserializer {

    // TODO In the future we could make the JSON process configurable, so application can choose between Jackson, GSON, and others
    // TODO There could be also value in letting application decide custom object mapper configuration
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    MoccaDeserializer() {
    }

    /**
     * Deserialize the response, from the provided input stream,
     * and bind it to an object whose type is also provided.
     * <br>
     * In case the GraphQL operation defined an empty result (common in mutations),
     * then this method will return an empty optional.
     *
     * @param inputStream the stream providing the bytes to be deserialized and mapped into the response type
     * @param responseType the type used to instantiate a new response object
     * @param operationName the name of the GraphQL operation whose response will result in the object to be returned
     * @return an optional with the deserialized and bound response object, which can be a list or not
     */
    Optional<?> deserialize(final InputStream inputStream, final Type responseType, final String operationName) {
        return MoccaReflection.getInnerType(responseType, List.class)
                .map(MoccaReflection::erase)
                .<Optional<?>>map(listParameterClass -> deserializeList(inputStream, listParameterClass, operationName))
                .orElseGet(() -> deserializeObject(inputStream, MoccaReflection.erase(responseType), operationName));
    }

    /*
     * Deserialize the response, from the provided input stream,
     * and bind it to a list of objects whose class is also provided.
     * It is expected the input stream contains a list of objects.
     * <br>
     * In case the GraphQL operation defined an empty result (common in mutations),
     * then this method will return an empty optional.
     *
     * @param inputStream the stream providing the bytes to be deserialized and mapped into the response type
     * @param listParameterClass the class of items in the list to be deserialized and returned
     * @param operationName the name of the GraphQL operation whose response will result in the list to be returned
     * @return an optional with the deserialized and bound response list
     */
    @SuppressWarnings({"unchecked"})
    private <T> Optional<List<T>> deserializeList(final InputStream inputStream, final Class<T> listParameterClass, final String operationName) {
        try {
            JsonNode dataNode = getDataNode(inputStream);

            if (!dataNode.isNull()) {
                JsonNode operationData = dataNode.path(operationName);
                Class<T[]> arrayClass = (Class<T[]>) Array.newInstance(listParameterClass, 0).getClass();
                T[] objects = objectMapper.treeToValue(operationData, arrayClass);
                return Optional.of(Arrays.asList(objects));
            }

            return Optional.empty();
        } catch (IOException e) {
            throw new MoccaException("Error processing response JSON payload", e);
        }
    }

    /*
     * Deserialize the response, from the provided input stream,
     * and bind it to an object whose class is also provided.
     * It is expected the input stream does NOT contain a list of objects, and class is NOT a list.
     * <br>
     * In case the GraphQL operation defined an empty result (common in mutations),
     * then this method will return an empty optional.
     *
     * @param inputStream the stream providing the bytes to be deserialized and mapped into the response type
     * @param responseClass the class (not a List) used to instantiate a new response object
     * @param operationName the name of the GraphQL operation whose response will result in the object to be returned
     * @return an optional with the deserialized and bound response object
     */
    private <T> Optional<T> deserializeObject(final InputStream inputStream, final Class<T> responseClass, final String operationName) {
        try {
            JsonNode dataNode = getDataNode(inputStream);

            if (!dataNode.isNull()) {
                JsonNode operationData = dataNode.path(operationName);
                if (operationData == null || operationData.toString().trim().equals("")) {
                    throw new MoccaException("Response JSON payload does not contain data for the requested operation: " + operationName);
                }
                return Optional.ofNullable(objectMapper.treeToValue(operationData, responseClass));
            }

            return Optional.empty();
        } catch (IOException e) {
            throw new MoccaException("Error processing response JSON payload", e);
        }
    }

    private JsonNode getDataNode(final InputStream inputStream) throws IOException {
        JsonNode responsePayload = objectMapper.readTree(inputStream);
        JsonNode errorsNode = responsePayload.path("errors");
        JsonNode dataNode;

        if (errorsNode.isMissingNode()){
            dataNode = responsePayload.path("data");
            if (dataNode.isMissingNode()) {
                throw new MoccaException("Response does not include data nor errors JSON fields");
            }
        } else {
            String exceptionMessage = getErrorsMessage(errorsNode);
            throw new MoccaException(exceptionMessage);
        }

        return dataNode;
    }

    private static String getErrorsMessage(final JsonNode errors) {
        if (errors.isNull()) {
            return "Response contains a null errors field";
        } else if(errors.size() == 0) {
            return "Response contains an empty errors list";
        } else if(errors.size() == 1) {
            return errors.get(0).path("message").asText();
        } else {
            return errors.toString();
        }
    }

}