package com.paypal.mocca.client;

/**
 * Utility methods for working consistently with arguments.
 * Similar to {@link java.util.Objects}.  It also has similarities
 * to Guava's Preconditions.  The functions almost always throw
 * {@link IllegalArgumentException} when requirements are not met.
 *
 * @author crankydillo@gmail.com
 */
final class Arguments {
    private Arguments() {}

    static void require(final boolean assertion) {
        if (!assertion) {
            throw new IllegalArgumentException();
        }
    }

    static void require(final boolean assertion, final String message) {
        if (!assertion) {
            throw new IllegalArgumentException(message);
        }
    }

    static <T> T requireNonNull(final T t) {
        return require(t, t != null);
    }

    static <T> T requireNonNull(final T t, final String message) {
        return require(t, t != null, message);
    }

    static <T> T require(T t, final boolean assertion) {
        if (!assertion) {
            throw new IllegalArgumentException();
        }
        return t;
    }

    static <T> T require(T t, final boolean assertion, final String message) {
        if (!assertion) {
            throw new IllegalArgumentException(message);
        }
        return t;
    }
}
