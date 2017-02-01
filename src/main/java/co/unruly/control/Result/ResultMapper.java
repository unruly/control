package co.unruly.control.Result;


import java.util.function.Function;

import static co.unruly.control.Result.Result.success;

/**
 * A function which takes in a Result, and outputs a value.
 */
@FunctionalInterface
public interface ResultMapper<S, F, T> extends Function<S, T> {

    T onResult(Result<S, F> r);

    default T apply(S initialValue) {
        return onResult(success(initialValue));
    }

    /**
     * Creates a ResultMapper from two functions: one to apply in the case of success, one in the case
     * of failure
     */
    static <S, F, T> ResultMapper<S, F, T> of(Function<S, T> onSuccess, Function<F, T> onFailure) {
        return r -> r.either(onSuccess, onFailure);
    }
}
