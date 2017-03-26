package co.unruly.control;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

public interface HigherOrderFunctions {

    /**
     * Takes a BiFunction, and reverses the order of the arguments
     */
    static <A, B, R> BiFunction<B, A, R> flip(BiFunction<A, B, R> f) {
        return (a, b) -> f.apply(b, a);
    }

    /**
     * Takes a list of functions (which take and return the same type) and composes
     * them into a single function, applying the provided functions in order
     */
    static <T> Function<T, T> compose(Function<T, T>... functions) {
        return Stream.of(functions).reduce(identity(), Function::andThen);
    }

    /**
     * Takes a value and applies a function to it.
     */
    static <S, F, T> T with(final S input, final Function<S, T> resultMapper) {
        return resultMapper.apply(input);
    }

    /**
     * Turns a Consumer into a Function which applies the consumer and returns the input
     */
    static <T> Function<T, T> peek(Consumer<T> action) {
        return t -> {
            action.accept(t);
            return t;
        };
    }

    static <R, T extends R> R upcast(T fv) {
        return fv;
    }
}
