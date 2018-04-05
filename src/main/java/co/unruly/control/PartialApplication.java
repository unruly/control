package co.unruly.control;

import co.unruly.control.pair.Triple.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A collection of functions to partially apply arguments to functions, to simplify usage
 * in streams, optionals etc.
 */
public interface PartialApplication {

    /**
     * Binds the provided argument to the function, and returns a Supplier with that argument applied.
     *
     * bind(f, a) is equivalent to () -> f.apply(a)
     */
    static <I, O> Supplier<O> bind(Function<I, O> f, I input) {
        return () -> f.apply(input);
    }

    /**
     * Binds the provided argument to the function, and returns a new Function with that argument already applied.
     *
     * bind(f, a) is equivalent to b -> f.apply(a, b)
     */
    static <A, B, R> Function<B, R> bind(BiFunction<A, B, R> f, A firstParam) {
        return secondParam -> f.apply(firstParam, secondParam);
    }

    /**
     * Binds the provided arguments to the function, and returns a new Supplier with those arguments already applied.
     *
     * bind(f, a, b) is equivalent to () -> f.apply(a, b)
     */
    static <A, B, R> Supplier<R> bind(BiFunction<A, B, R> f, A firstParam, B secondParam) {
        return () -> f.apply(firstParam, secondParam);
    }

    /**
     * Binds the provided argument to the function, and returns a new BiFunction with that argument already applied.
     *
     * bind(f, a) is equivalent to (b, c) -> f.apply(a, b, c)
     */
    static <A, B, C, R> BiFunction<B, C, R> bind(TriFunction<A, B, C, R> f, A firstParam) {
        return (secondParam, thirdParam) -> f.apply(firstParam, secondParam, thirdParam);
    }

    /**
     * Binds the provided arguments to the function, and returns a new Function with those arguments already applied.
     *
     * bind(f, a, b) is equivalent to c -> f.apply(a, b, c)
     */
    static <A, B, C, R> Function<C, R> bind(TriFunction<A, B, C, R> f, A firstParam, B secondParam) {
        return thirdParam -> f.apply(firstParam, secondParam, thirdParam);
    }
}
