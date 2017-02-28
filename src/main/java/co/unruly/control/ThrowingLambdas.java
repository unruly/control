package co.unruly.control;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A collection of functional interfaces which throw, and convenience functions to wrap them
 * so any thrown exceptions are converted to RuntimeExceptions so they can be used where
 * non-throwing functional interfaces are required
 */
public class ThrowingLambdas {

    /**
     * A Function which may throw a checked exception
     */
    @FunctionalInterface
    public interface ThrowingFunction<I, O, X extends Exception> {
        O apply(I input) throws X;

        /**
         * Converts the provided function into a regular Function, where any thrown exceptions are
         * wrapped in a RuntimeException.
         */
        static <I, O, X extends Exception> Function<I, O> throwingRuntime(ThrowingFunction<I, O, X> f) {
            return x -> {
                try {
                    return f.apply(x);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            };
        }
    }

    /**
     * A Consumer which may throw a checked exception
     */
    @FunctionalInterface
    public interface ThrowingConsumer<T, X extends Exception> {
        void accept(T item) throws X;

        /**
         * Converts the provided consumer into a regular Consumer, where any thrown exceptions are
         * wrapped in a RuntimeException.
         */
        static <T, X extends Exception> Consumer<T> throwingRuntime(ThrowingConsumer<T, X> p) {
            return x -> {
                try {
                    p.accept(x);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            };
        }
    }

    /**
     * A BiFunction which may throw a checked exception
     */
    @FunctionalInterface
    public interface ThrowingBiFunction<A, B, R, X extends Exception> {
        R apply(A first, B second) throws X;

        /**
         * Converts the provided bifunction into a regular BiFunction, where any thrown exceptions
         * are wrapped in a RuntimeException
         */
        static <A, B, R, X extends Exception> BiFunction<A, B, R> throwingRuntime(ThrowingBiFunction<A, B, R, X> f) {
            return (a, b) -> {
                try {
                    return f.apply(a, b);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            };
        }
    }

    /**
     * A Predicate which may throw a checked exception
     */
    @FunctionalInterface
    public interface ThrowingPredicate<T, X extends Exception> {
        boolean test(T item) throws X;

        /**
         * Converts the provided predicate into a regular Predicate, where any thrown exceptions
         * are wrapped in a RuntimeException
         */
        static <T, X extends Exception> Predicate<T> throwingRuntime(ThrowingPredicate<T, X> p) {
            return x -> {
                try {
                    return p.test(x);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            };
        }
    }
}
