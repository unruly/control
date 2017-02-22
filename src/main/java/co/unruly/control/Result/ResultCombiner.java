package co.unruly.control.Result;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ResultCombiner<A, B, T, F> extends BiFunction<Result<A, F>, Result<B, F>, Result<T, F>> {
}
