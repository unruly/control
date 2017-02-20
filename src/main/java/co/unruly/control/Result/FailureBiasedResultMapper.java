package co.unruly.control.Result;

import java.util.function.Function;

import static co.unruly.control.Result.Result.failure;

@FunctionalInterface
public interface FailureBiasedResultMapper<S, F, T> extends Function<F, T> {

    T onResult(Result<S, F> r);

    default T apply(F initialValue) {
        return onResult(failure(initialValue));
    }

    default T fromFailure(F initialValue) { return onResult(failure(initialValue)); }

    /**
     * Creates a ResultMapper from two functions: one to apply in the case of success, one in the case
     * of failure
     */
    static <S, F, T> ResultMapper<S, F, T> of(Function<S, T> onSuccess, Function<F, T> onFailure) {
        return r -> r.either(onSuccess, onFailure);
    }
}
