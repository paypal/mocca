package com.paypal.mocca.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to set HTTP header dynamic values.
 * It can only be used at the method level.
 * See documentation at {@link RequestHeader} for further information.
 *
 * @see RequestHeader
 * @author abprabhakar@paypal.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RequestHeaderParam {

    /**
     * Sets the name of the header value placeholder defined in the {@link RequestHeader} annotation.
     * See documentation at {@link RequestHeader} for further information.
     * @return the name of the header value placeholder
     */
    String value();

}
