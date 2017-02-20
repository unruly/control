package co.unruly.control.Result;

import co.unruly.control.Pair;
import co.unruly.control.Unit;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static co.unruly.control.Result.Result.failure;
import static co.unruly.control.Unit.functify;
import static java.util.function.Function.identity;

public class Results {

    /*******************************************************
     * Pipeline operations:
     *   These operations convert a Result into another
     *   Result, and can be composed into a functional pipeline
     ******************************************************/

    /**
     * Allows introduction of types, if the early operations in a chain are
     * unable to infer types and the notation is preferable to explicit generics
     */
    public static <S, F> EndoAttempt<S, F> startingWith(Class<S> s, Class<F> f) {
        return r -> r;
    }

    /**
     * Fires an event if the result is a success, returning the unchanged Result
     * in either case
     */
    public static <S, F> EndoAttempt<S, F> ifSuccess(Consumer<S> c) {
        return r -> {
            r.either(functify(c), Unit::noOp);
            return r;
        };
    }

    /**
     * Fires an event if the result is a failure, returning the unchanged Result
     * in either case
     */
    public static <S, F> EndoAttempt<S, F> ifFailure(Consumer<F> c) {
        return r -> {
            r.either(Unit::noOp, functify(c));
            return r;
        };
    }

    public static <S, S1, F> Attempt<S, S1, F, F> map(Function<S, S1> f) {
        return flatMap(f.andThen(Result::success));
    }

    public static <S, S1, F> Attempt<S, S1, F, F> flatMap(Function<S, Result<S1, F>> f) {
        return r -> r.either(f, Result::failure);
    }

    public static <S, F, F1> Attempt<S, S, F, F1> mapFailures(Function<F, F1> f) {
        return flatMapFailures(f.andThen(Result::failure));
    }

    public static <S, F, F1> Attempt<S, S, F, F1> flatMapFailures(Function<F, Result<S, F1>> f) {
        return r -> r.either(Result::success, f);
    }

    /**
     * Flips an attempt, so successes are now considered failures and vice versa
     */
    public static <S, F> Attempt<S, F, F, S> invert() {
        return r -> r.either(Result::failure, Result::success);
    }

    public static <S, S1> Attempt<S, S1, Exception, Exception> tryTo(Attempt<S, S1, Exception, Exception> f) {
        return r -> {
            try {
                return f.onResult(r);
            } catch (Exception ex) {
                return failure(ex);
            }
        };
    }

    public static <S, S1, F, F1> Attempt<S, S1, F, F1> tryTo(
            Attempt<S, S1, F, F1> f,
            Function<Exception, F1> exceptionHandler) {
        return r -> {
            try {
                return f.onResult(r);
            } catch (Exception ex) {
                return failure(exceptionHandler.apply(ex));
            }
        };
    }

    /*******************************************************
     * Terminal operations:
     *   These operations convert a Result into a value other
     *   than a Result, to terminate a functional pipeline
     ******************************************************/

    /**
     * Converts a Result where the success and failure types match
     * into the success or failure value, as appropriate
     */
    public static <T> ResultMapper<T, T, T> collapse() {
        return ResultMapper.of(identity(), identity());
    }

    /**
     * Converts a Result into either an Optional containing the success
     * value, or an empty Optional if it's a failure.
     */
    public static <S, F> ResultMapper<S, F, Optional<S>> asOptional() {
        return ResultMapper.of(Optional::of, __ -> Optional.empty());
    }

    /**
     * Converts a Result into either an Optional containing the failure
     * value, or an empty Optional if it's a success.
     */
    public static <S, F> ResultMapper<S, F, Optional<F>> failureAsOptional() {
        return ResultMapper.of(__ -> Optional.empty(), Optional::of);
    }

    /**
     * Converts an Optional into a Result, using the failure generator
     * if the Optional is empty.
     */
    public static <S, F> Result<S, F> fromOptional(Optional<S> maybeValue, Supplier<F> failureGenerator) {
        return maybeValue.map(Result::<S, F>success).orElseGet(() -> Result.failure(failureGenerator.get()));
    }

    /**
     * Returns the success value, if this is a Success, or the result of calling the
     * provided function on the failure value if this is a failure
     */
    public static <S, F> ResultMapper<S, F, S> orElseGet(Function<F, S> supplier) {
        return ResultMapper.of(identity(), supplier);
    }

    public static <S, F> Consumer<Result<S, F>> onSuccess(Consumer<S> c) {
        return r -> r.either(functify(c), Unit::noOp);
    }

    public static <F> Consumer<Result<?, F>> onFailure(Consumer<F> c) {
        return r -> r.either(Unit::noOp, functify(c));
    }

    /*******************************************************
     * Streaming operations:
     *   These operations are to facilitate streaming over Results
     ******************************************************/

    /**
     * Converts this Result to a stream of either one success value or
     * no values, intended to be flatmapped over by a stream of Results
     * to convert it into a stream of success values.
     */
    public static <S, F> Function<Result<S, F>, Stream<S>> successes() {
        return r -> r.either(Stream::of, f -> Stream.empty());
    }

    /**
     * Converts this Result to a stream of either one failure value or
     * no values, intended to be flatmapped over by a stream of Results
     * to convert it into a stream of failure values.
     */
    public static <S, F> Function<Result<S, F>, Stream<F>> failures() {
        return r -> r.either(__ -> Stream.empty(), Stream::of);
    }

    /**
     * Collects a Stream of Results into a Pair of Lists, the left containing the unwrapped
     * success values, the right containing the unwrapped failures.
     */
    public static <S, F> Collector<Result<S, F>, Pair<List<S>, List<F>>, Pair<List<S>, List<F>>> split() {
        return new ResultCollector<>();
    }
}
