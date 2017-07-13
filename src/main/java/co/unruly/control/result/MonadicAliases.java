package co.unruly.control.result;

import java.util.function.Function;

import static co.unruly.control.result.Introducers.success;
import static co.unruly.control.result.Transformers.*;

/**
 * Aliases for standard functions on Results which use names more familiar
 * to users of Haskell
 */
public interface MonadicAliases {

    /**
     * Returns a function which converts a regular value into a Result (as a Success)
     */
    static <S, F> Function<S, Result<S, F>> pure() {
        return success();
    }

    /**
     * Returns a function which, when applied to a Result, applies the provided function to
     * the wrapped value if it's a Success, otherwise perpetuates the existing failure
     */
    static <S, S1, F> Function<Result<S, F>, Result<S1, F>> map(Function<S, S1> f) {
        return onSuccess(f);
    }

    /**
     * Returns a function which, when applied to a Result, applies the provided function to
     * the wrapped value, returning that Result, if it's a Success. This can turn a Success into a
     * Failure.
     *
     * If the result was already a failure, it perpetuates the existing failure.
     */
    static <S, S1, F> Function<Result<S, F>, Result<S1, F>> flatMap(Function<S, Result<S1, F>> f) {
        return attempt(f);
    }

    /**
     * Returns a function which, when applied to a Result, applies the provided function to
     * the wrapped value, returning that Result, if it's a Success. This can turn a Success into a
     * Failure.
     *
     * If the result was already a failure, it perpetuates the existing failure.
     */
    static <S, S1, F> Function<Result<S, F>, Result<S1, F>> bind(Function<S, Result<S1, F>> f) {
        return attempt(f);
    }

    /**
     * Returns a function which, when applied to a Result, applies the provided function to
     * the wrapped value if it's a failure, otherwise perpetuates the existing success
     */
    static <S, F, F1> Function<Result<S, F>, Result<S, F1>> mapFailure(Function<F, F1> f) {
        return onFailure(f);
    }

    /**
     * Returns a function which, when applied to a Result, applies the provided function to
     * the wrapped value, returning that Result, if it's a Failure. This can turn a Failure into a
     * Success.
     *
     * If the result was already a success, it perpetuates the existing success.
     */
    static <S, F, F1> Function<Result<S, F>, Result<S, F1>> flatMapFailure(Function<F, Result<S, F1>> f) {
        return recover(f);
    }


    /**
     * Returns a function which, when applied to a Result, applies the provided function to
     * the wrapped value, returning that Result, if it's a Failure. This can turn a Failure into a
     * Success.
     *
     * If the result was already a success, it perpetuates the existing success.
     */
    static <S, F, F1> Function<Result<S, F>, Result<S, F1>> bindFailure(Function<F, Result<S, F1>> f) {
        return recover(f);
    }
}
