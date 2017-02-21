package co.unruly.control.Result;

import java.util.stream.Stream;

/**
 * Convenience functional interface for when an Attempt has the same input and output types,
 * as the type signatures can get very noisy otherwise.
 */
@FunctionalInterface
public interface EndoAttempt<S, F> extends Attempt<S, S, F, F> {

    /**
     * Compose two EndoAttempts together, executing first this EndoAttempt and then the provided EndoAttempt.
     */
    default EndoAttempt<S, F> then(EndoAttempt<S, F> f) {
        return r -> f.onResult(onResult(r));
    }

    static <S, F>  EndoAttempt<S, F> identity() {
        return result -> result;
    }

    static <S, F> EndoAttempt<S, F> compose(EndoAttempt<S, F> ...attempts) {
        return Stream.of(attempts).reduce(i -> i, EndoAttempt::then);
    }
}
