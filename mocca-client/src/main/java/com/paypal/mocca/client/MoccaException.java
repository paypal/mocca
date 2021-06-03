package com.paypal.mocca.client;

/**
 * Unchecked exception thrown when an
 * unexpected error happened during a Mocca call.
 *
 * @author fabiocarvalho777@gmail.com
 */
public class MoccaException extends RuntimeException {

    public MoccaException(String message) {
        super(message);
    }

    public MoccaException(String message, Exception cause) {
        super(message, cause);
    }

}