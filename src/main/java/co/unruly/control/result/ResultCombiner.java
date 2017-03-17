package co.unruly.control.result;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ResultCombiner<A, B, T, F> extends BiFunction<Result<A, F>, Result<B, F>, Result<T, F>> {
}
