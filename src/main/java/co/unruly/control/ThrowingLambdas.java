package co.unruly.control;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ThrowingLambdas {

    @FunctionalInterface
    public interface ThrowingFunction<I, O, X extends Exception> {
        O apply(I input) throws X;

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

    @FunctionalInterface
    public interface ThrowingConsumer<T, X extends Exception> {
        void accept(T item) throws X;

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

    @FunctionalInterface
    public interface ThrowingBiFunction<A, B, R, X extends Exception> {
        R apply(A first, B second) throws X;

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

    @FunctionalInterface
    public interface ThrowingPredicate<T, X extends Exception> {
        boolean test(T item) throws X;

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
