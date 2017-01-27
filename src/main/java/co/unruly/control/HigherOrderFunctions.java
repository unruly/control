package co.unruly.control;

import java.util.function.BiFunction;

public class HigherOrderFunctions {

    public static <A, B, R> BiFunction<B, A, R> flip(BiFunction<A, B, R> f) {
        return (a, b) -> f.apply(b, a);
    }
}
