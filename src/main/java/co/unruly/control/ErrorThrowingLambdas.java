package co.unruly.control;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A collection of functional interfaces which throw, and convenience functions to wrap them
 * so any thrown Throwables are converted to RuntimeExceptions so they can be used where
 * non-throwing functional interfaces are required
 *
 * Catching errors in the general case is not recommended, but there are specific errors
 * which are contextually reasonable to catch. Therefore, this wider capability exists
 * separately and should be used judiciously.
 */
public interface ErrorThrowingLambdas {

    /**
     * A Function which may throw a checked exception
     */
    @FunctionalInterface
    interface ThrowingFunction<I, O, X extends Throwable> {
        O apply(I input) throws X;

        default <T> ThrowingFunction<I, T, X> andThen(Function<O, T> nextFunction) {
            return x -> nextFunction.apply(apply(x));
        }

        default <T> ThrowingFunction<T, O, X> compose(Function<T, I> nextFunction) {
            return x -> apply(nextFunction.apply(x));
        }

        /**
         * Converts the provided function into a regular Function, where any thrown exceptions are
         * wrapped in a RuntimeException.
         */
        static <I, O, X extends Throwable> Function<I, O> throwingRuntime(ThrowingFunction<I, O, X> f) {
            return x -> {
                try {
                    return f.apply(x);
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            };
        }
    }

    /**
     * A Consumer which may throw a checked exception
     */
    @FunctionalInterface
    interface ThrowingConsumer<T, X extends Throwable> {
        void accept(T item) throws X;

        /**
         * Converts the provided consumer into a regular Consumer, where any thrown exceptions are
         * wrapped in a RuntimeException.
         */
        static <T, X extends Throwable> Consumer<T> throwingRuntime(ThrowingConsumer<T, X> p) {
            return x -> {
                try {
                    p.accept(x);
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            };
        }
    }

    /**
     * A BiFunction which may throw a checked exception
     */
    @FunctionalInterface
    interface ThrowingBiFunction<A, B, R, X extends Throwable> {
        R apply(A first, B second) throws X;

        /**
         * Converts the provided bifunction into a regular BiFunction, where any thrown exceptions
         * are wrapped in a RuntimeException
         */
        static <A, B, R, X extends Throwable> BiFunction<A, B, R> throwingRuntime(ThrowingBiFunction<A, B, R, X> f) {
            return (a, b) -> {
                try {
                    return f.apply(a, b);
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            };
        }
    }

    /**
     * A Predicate which may throw a checked exception
     */
    @FunctionalInterface
    interface ThrowingPredicate<T, X extends Throwable> {
        boolean test(T item) throws X;

        /**
         * Converts the provided predicate into a regular Predicate, where any thrown exceptions
         * are wrapped in a RuntimeException
         */
        static <T, X extends Throwable> Predicate<T> throwingRuntime(ThrowingPredicate<T, X> p) {
            return x -> {
                try {
                    return p.test(x);
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            };
        }
    }
}
