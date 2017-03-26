package co.unruly.control.result;

import co.unruly.control.Optionals;
import co.unruly.control.Pair;
import co.unruly.control.ThrowingLambdas;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Stream.empty;

/**
 * A set of common functions to convert from a <code>Result</code> to
 * an unwrapped value.
 */
public interface Resolvers {

    /**
     * Takes a Result where both success and failure types are the same, and returns
     * either the success or failure value as appropriate
     */
    static <T> Function<Result<T, T>, T> collapse() {
        return r -> r.either(identity(), identity());
    }

    /**
     * Takes a Result and returns the success value if it is a success, or if it's
     * a failure, returns the result of applying the recovery function to the
     * failure value.
     */
    static <OS, IS extends OS, FS extends OS, F> Function<Result<IS, F>, OS> ifFailed(Function<F, FS> recoveryFunction) {
        return r -> r.either(identity(), recoveryFunction);
    }

    /**
     * Takes a Result for which the failure type is an Exception, and returns the
     * success value if it's a success, or throws the failure exception, wrapped in a
     * RuntimeException.
     */
    static <S, X extends Exception> Function<Result<S, X>, S> getOrThrow() {
        return r -> r.either(identity(), ex -> { throw new RuntimeException(ex); });
    }

    /**
     * Takes a Result and returns the success value if it is a success, or if it's
     * a failure, throws the result of applying the exception converter to the
     * failure value.
     */
    static <S, F> Function<Result<S, F>, S> getOrThrow(Function<F, RuntimeException> exceptionConverter) {
        return r -> r.either(identity(), failure -> { throw exceptionConverter.apply(failure); });
    }

    /**
     * Returns a Stream of successes: a stream of a single value if this is a success,
     * or an empty stream if this is a failure. This is intended to be used to flat-map
     * over a stream of Results to extract a stream of just the successes.
     */
    static <S, F> Function<Result<S, F>, Stream<S>> successes() {
        return r -> r.either(Stream::of, __ -> empty());
    }

    /**
     * Returns a Stream of failures: a stream of a single value if this is a failure,
     * or an empty stream if this is a success. This is intended to be used to flat-map
     * over a stream of Results to extract a stream of just the failures.
     */
    static <S, F> Function<Result<S, F>, Stream<F>> failures() {
        return r -> r.either(__ -> empty(), Stream::of);
    }

    /**
     * Returns an Optional success value, which is present if this result was a failure
     * and empty if it was a failure.
     */
    static <S, F> Function<Result<S, F>, Optional<S>> toOptional() {
        return r -> r.either(Optional::of, __ -> Optional.empty());
    }

    /**
     * Returns an Optional failure value, which is present if this result was a failure
     * and empty if it was a success.
     */
    static <S, F> Function<Result<S, F>, Optional<F>> toOptionalFailure() {
        return r -> r.either(__ -> Optional.empty(), Optional::of);
    }

    /**
     * Collects a Stream of Results into a Pair of Lists, the left containing the unwrapped
     * success values, the right containing the unwrapped failures.
     */
    public static <S, F> Collector<Result<S, F>, Pair<List<S>, List<F>>, Pair<List<S>, List<F>>> split() {
        return new ResultCollector<>();
    }
}
