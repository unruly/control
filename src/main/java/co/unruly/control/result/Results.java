package co.unruly.control.result;

import co.unruly.control.Pair;
import co.unruly.control.Unit;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static co.unruly.control.Unit.functify;
import static co.unruly.control.result.Result.success;
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
    public static <S, F> Function<Result<S, F>, Result<S, F>> startingWith(Class<S> s, Class<F> f) {
        return r -> r;
    }

    /**
     * Fires an event if the result is a success, returning the unchanged Result
     * in either case
     */
    public static <S, F> ConsumableFunction<Result<S, F>, Result<S, F>> onSuccess(Consumer<S> c) {
        return r -> {
            r.either(functify(c), Unit::noOp);
            return r;
        };
    }

    /**
     * Fires an event if the result is a failure, returning the unchanged Result
     * in either case
     */
    public static <S, F> ConsumableFunction<Result<S, F>, Result<S, F>> onFailure(Consumer<F> c) {
        return r -> {
            r.either(Unit::noOp, functify(c));
            return r;
        };
    }

    /**
     * If the result is a Success, applies the given function to that value and wraps
     * it in a new Success. Otherwise, returns the Failure
     */
    public static <S, S1, F> Function<Result<S, F>, Result<S1, F>> map(Function<S, S1> f) {
        return flatMap(f.andThen(Result::success));
    }

    /**
     * If the result is a Success, applies the given function to that value (which
     * could return either a Success or Failure). Otherwise, returns the Failure.
     */
    public static <S, S1, F, FF extends F> Function<Result<S, F>, Result<S1, F>> flatMap(Function<S, Result<S1, FF>> f) {
        return r -> r.either(
            success -> f.apply(success).then(mapFailure(Results::upcast)),
            Result::failure);
    }

    /**
     * If the result is a Failure, applies the given function to that value and wraps
     * it in a new Failure. Otherwise, returns the Success
     */
    public static <S, F, F1> Function<Result<S, F>, Result<S, F1>> mapFailure(Function<F, F1> f) {
        return flatMapFailure(f.andThen(Result::failure));
    }

    /**
     * If the result is a Failure, applies the given function to that value (which
     * could return either a Success or Failure). Otherwise, returns the Success.
     */
    public static <S, F, F1> Function<Result<S, F>, Result<S, F1>> flatMapFailure(Function<F, Result<S, F1>> f) {
        return r -> r.either(Result::success, f);
    }

    /**
     * Flips an attempt, so successes are now considered failures and vice versa
     */
    public static <S, F> Function<Result<S, F>, Result<F, S>> invert() {
        return r -> r.either(Result::failure, Result::success);
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
    public static <T> Function<Result<T, T>, T> collapse() {
        return r -> r.either(identity(), identity());
    }

    /**
     * Takes a Result where the failure type is an Exception, and either
     * returns the Success value or throws the Exception (wrapped in a
     * RuntimeException)
     */
    public static <T> Function<Result<T, Exception>, T> getOrThrow() {
        Function<Exception, T> onFailure = ex -> { throw new RuntimeException(ex); };
        return r -> r.either(identity(), onFailure);
    }

    /**
     * Returns the success value, if this is a Success, or the result of calling the
     * provided function on the failure value if this is a failure
     */
    public static <OS, F, IS extends OS> Function<Result<IS, F>, OS> ifFailed(Function<F, OS> supplier) {
        Function<IS, OS> onSuccess = i->i;
        return r -> r.either(onSuccess, supplier);
    }

    /**
     * Converts a Result into either an Optional containing the success
     * value, or an empty Optional if it's a failure.
     */
    public static <S, F> Function<Result<S, F>, Optional<S>> asOptional() {
        return result -> result.either(Optional::of, __ -> Optional.empty());
    }

    /**
     * Converts a Result into either an Optional containing the failure
     * value, or an empty Optional if it's a success.
     */
    public static <S, F> Function<Result<S, F>, Optional<F>> failureAsOptional() {
        Function<S, Optional<F>> onSuccess = __ -> Optional.empty();
        return r -> r.either(onSuccess, Optional::of);
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

    /*******************************************************
     * Conversion operations:
     *   These operations are to facilitate creating a new
     *   Result from something other than a Result
     ******************************************************/

    /**
     * Converts an Optional into a Result, using the failure generator
     * if the Optional is empty.
     */
    public static <S, F> Result<S, F> fromOptional(Optional<S> maybeValue, Supplier<F> failureGenerator) {
        return maybeValue.map(Result::<S, F>success).orElseGet(() -> Result.failure(failureGenerator.get()));
    }

    /**
     * Combines two Results into a single Result. If both arguments are a Success, then
     * it applies the given function to their values and returns a Success of it.
     *
     * If either or both arguments are Failures, then this returns the first failure
     * it encountered.
     */
    public static <A, B, F> Function<Result<A, F>, MergeableResults<A, B, F>> combineWith(Result<B, F> secondArgument) {
        // ugh ugh ugh we need an abstract class because otherwise it can't infer generics properly can i be sick now? ta
        return result -> new MergeableResults<A, B, F>() {
            @Override
            public <C> Result<C, F> using(BiFunction<A, B, C> combiner) {
                return result.either(
                    s1 -> secondArgument.either(
                        s2 -> success(combiner.apply(s1, s2)),
                        Result::failure
                    ),
                    Result::failure
                );
            }
        };
    }

    @FunctionalInterface
    public interface MergeableResults<A, B, F>  {
        <C> Result<C, F> using(BiFunction<A, B, C> combiner);
    }

    private static <R, T extends R> R upcast(T fv) {
        return fv;
    }

}
