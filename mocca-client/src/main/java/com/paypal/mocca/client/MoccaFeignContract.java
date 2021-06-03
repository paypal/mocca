package com.paypal.mocca.client;

import com.paypal.mocca.client.annotation.Mutation;
import com.paypal.mocca.client.annotation.Query;
import com.paypal.mocca.client.annotation.RequestHeader;
import com.paypal.mocca.client.annotation.RequestHeaderParam;
import feign.DeclarativeContract;
import feign.MethodMetadata;
import feign.Request;
import feign.Util;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mocca Feign contract class, allowing Mocca to
 * dynamically configure target interfaces
 * with the necessary Feign annotations
 *
 * @author crankydillo@gmail.com
 */
class MoccaFeignContract extends DeclarativeContract {

    MoccaFeignContract() {
        super.registerClassAnnotation(RequestHeader.class, this::registerHeaderClassAnnotation);
        super.registerMethodAnnotation(Query.class, (annotation, metadata) -> registerAnnotations(metadata));
        super.registerMethodAnnotation(Mutation.class, (annotation, metadata) -> registerAnnotations(metadata));
        super.registerMethodAnnotation(RequestHeader.class, this::registerHeaderMethodAnnotation);
        super.registerParameterAnnotation(RequestHeaderParam.class, this::registerParamAnnotations);
    }

    //TODO: Need to add unit tests for since contract has logic now
    //TODO: JSON values are not supported yet.  Need to add encoding feature.
    private void registerAnnotations(MethodMetadata metadata) {
        metadata.template()
                .method(Request.HttpMethod.POST)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    /**
     * Registering {@link RequestHeader} annotation at class level.  Followed same approach as in
     * Feign's default contract.
     *
     * @param header RequestHeader annotation
     * @param metadata Method metadata
     */
    private void registerHeaderClassAnnotation(RequestHeader header, MethodMetadata metadata) {
        final String[] headersOnType = header.value();
        Util.checkState(headersOnType.length > 0, "RequestHeader annotation was empty on type %s.",
                metadata.configKey());
        final Map<String, Collection<String>> headers = toMap(headersOnType);
        verifyNoDynamicValue(headers);
        headers.putAll(metadata.template().headers());
        metadata.template().headers(null); // to clear
        metadata.template().headers(headers);
    }

    /**
     * Registering {@link RequestHeader} annotation at method level.  Followed same approach as in
     * Feign's default contract.
     *
     * @param header RequestHeader annotation
     * @param metadata Method metadata
     */
    private void registerHeaderMethodAnnotation(RequestHeader header, MethodMetadata metadata) {
        final String[] headersOnMethod = header.value();
        Util.checkState(headersOnMethod.length > 0, "RequestHeader annotation was empty on method %s.",
                metadata.configKey());
        metadata.template().headers(toMap(headersOnMethod));
    }

    /**
     * Registering {@link RequestHeaderParam} annotation at method level.  Followed same approach as in
     * Feign's default contract.
     *
     * @param headerParam RequestHeaderParam annotation
     * @param metadata Method metadata
     */
    private void registerParamAnnotations(RequestHeaderParam headerParam, MethodMetadata metadata, int paramIndex) {

        final String annotationName = headerParam.value();
        final Parameter parameter = metadata.method().getParameters()[paramIndex];
        final String name;
        if (Util.emptyToNull(annotationName) == null && parameter.isNamePresent()) {
            name = parameter.getName();
        } else {
            name = annotationName;
        }
        Util.checkState(Util.emptyToNull(name) != null, "Param annotation was empty on param %s.",
                paramIndex);
        nameParam(metadata, name, paramIndex);
    }

    private static Map<String, Collection<String>> toMap(String[] input) {
        final Map<String, Collection<String>> result =
                new LinkedHashMap<>(input.length);
        for (final String header : input) {
            final int colon = header.indexOf(':');
            final String name = header.substring(0, colon);
            if (!result.containsKey(name)) {
                result.put(name, new ArrayList<>(1));
            }
            result.get(name).add(header.substring(colon + 1).trim());
        }
        return result;
    }

    /**
     * Verifies that no dynamic value is set at the class level.  Dynamic
     * value is anything which starts with "{" character.  Of course this
     * will create problem for JSON values for which we have to use encoding
     * feature.
     *
     * @param headers all headers
     */
    private void verifyNoDynamicValue(Map<String, Collection<String>> headers) {
        headers.entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .forEach(v -> {
                    if (v.startsWith("{")) {
                        throw new MoccaException("Header value:" + v + " at class level cannot be dynamic");
                    }
                });
    }
}
