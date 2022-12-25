package me.fhoz.chunkydiscord.util;

/**
 * A utility class for checking conditions and throwing appropriate exceptions if they are not met.
 */
public class Check {
    /**
     * Throws a {@link NullPointerException} if the given object is {@code null}.
     *
     * @param t the object to check for {@code null}
     * @param exception the message to include in the {@link NullPointerException} if the object is {@code null}
     * @param <T> the type of the object
     * @throws NullPointerException if the object is {@code null}
     */
    public static <T> void notNull(T t, String exception) {
        if (t == null) {
            throw new NullPointerException(exception);
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the given expression is {@code false}.
     *
     * @param expression the expression to check
     * @param exception the message to include in the {@link IllegalArgumentException} if the expression is {@code false}
     * @throws IllegalArgumentException if the expression is {@code false}
     */
    public static void isTrue(boolean expression, String exception) {
        if (!expression) {
            throw new IllegalArgumentException(exception);
        }
    }
}
