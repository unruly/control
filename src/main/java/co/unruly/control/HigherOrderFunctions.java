package co.unruly.control;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

public class HigherOrderFunctions {

    /**
     * Takes a BiFunction, and reverses the order of the arguments
     */
    public static <A, B, R> BiFunction<B, A, R> flip(BiFunction<A, B, R> f) {
        return (a, b) -> f.apply(b, a);
    }

    /**
     * Takes a list of functions (which take and return the same type) and composes
     * them into a single function
     */
    public static <T> Function<T, T> compose(Function<T, T>... functions) {
        return Stream.of(functions).reduce(identity(), Function::andThen);
    }
}
