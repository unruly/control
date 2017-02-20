package co.unruly.control.Result;

import java.util.function.Function;

@FunctionalInterface
public interface FailureBiasedEndoAttempt<S, F> extends Function<F, Result<S, F>> {

    Result<S, F> onResult(Result<S, F> r);

    default Result<S, F> apply(F initialFailure) {
        return onResult(Result.failure(initialFailure));
    }

    /**
     * Compose two Attempts together, executing first this Attempt and then the provided Attempt.
     */
    default FailureBiasedEndoAttempt<S, F> then(FailureBiasedEndoAttempt<S, F> f) {
        return r -> f.onResult(onResult(r));
    }

    /**
     * Compose this Attempt with a terminal ResultMapper to yield a function which outputs a simple (non-Result)
     * value.
     */
    default <T> FailureBiasedResultMapper<S, F, T> andFinally(ResultMapper<S, F, T> terminal) {
        return r -> terminal.onResult(onResult(r));
    }

}
