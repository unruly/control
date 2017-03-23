package co.unruly.control.result;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static co.unruly.control.HigherOrderFunctions.compose;
import static co.unruly.control.result.Casts.cast;
import static co.unruly.control.result.Result.failure;
import static co.unruly.control.result.Results.*;

/**
 * A small DSL for building compact dispatch tables: better than if-expressions, worse than
 * proper pattern matching. But hey, it's Java, what do you expect?
 *
 * This models a match attempt as a sequence of operations on a Result, starting with a Failure
 * and continuously trying to use flatMapFailure to convert that Result into a Success.
 */
public class Match {

    /**
     * Builds a dispatch function from the provided matchers. Note that in order to yield
     * a function, the otherwise() method must be called on the result of this function:
     * as there's no way to determine if the dispatch table is complete, a base case is
     * required.
     */
    @SafeVarargs
    public static <I, O> MatchAttempt<I, O> match(Function<Result<O, I>, Result<O, I>>... potentialMatchers) {
        return f -> {
            Function<Result<O, I>, O> after = ifFailed(f);
            return attemptMatch(potentialMatchers).andThen(after).compose(Result::success);
        };
    }

    /**
     * Dispatches a value across the provided matchers. Note that in order to yield
     * a value, the otherwise() method must be called on the result of this function:
     * as there's no way to determine if the dispatch table is complete, a base case is
     * required.
     */
    @SafeVarargs
    public static <I, O> BoundMatchAttempt<I, O> matchValue(I inputValue, Function<Result<O, I>, Result<O, I>>... potentialMatchers) {
        return f -> attemptMatch(potentialMatchers).andThen(ifFailed(f)).apply(Result.success(inputValue));
    }

    /**
     * Matches the value if it's of the same type (or a subtype of) the class specified.
     * The handler function provided is flow-typed to take arguments of the specified class,
     * not the general type of objects being matched.
     */
    public static <S, F, F1 extends F> Function<Result<S, F>, Result<S, F>> ifType(Class<F1> type, Function<F1, S> function) {
        return x -> x.then(flatMapFailure(
                failure -> cast(failure, type)
                    .then(flatMap(matchedType -> Result.success(function.apply(matchedType))))
        ));
    }

    /**
     * Matches the value if it passes the provided predicate, and applies the handler function
     * provided to it.
     */
    public static <S, F> Function<Result<S, F>, Result<S, F>> ifIs(Predicate<F> predicate, Function<F, S> function) {
        return x -> x.then(flatMapFailure(
                failure -> predicate.test(failure)
                    ? Result.success(function.apply(failure))
                    : failure(failure)
        ));
    }

    /**
     * Matches the value if it is equal to the provided value, and applies the handler
     * function to it.
     */
    public static <S, F> Function<Result<S, F>, Result<S, F>> ifEquals(F value, Function<F, S> function) {
        return ifIs(value::equals, function);
    }

    /**
     * Matches the value if the provided function yields an Optional whose value is
     * present, returning the value in that Optional.
     */
    public static <S, F> Function<Result<S, F>, Result<S, F>> ifPresent(Function<F, Optional<S>> successProvider) {
        return r -> r.then(flatMapFailure(
            failure -> successProvider
                .apply(failure)
                .map(Result::<S, F>success)
                .orElseGet(() -> failure(failure))
            )
        );
    }

    @SafeVarargs
    private static <S, F> Function<Result<S, F>, Result<F, S>> attemptMatch(Function<Result<F, S>, Result<F, S>>... potentialMatches) {
        return Results.<S, F>invert().andThen(compose(potentialMatches));
    }

    @FunctionalInterface
    public interface MatchAttempt<I, O> {
        Function<I, O> otherwise(Function<I, O> baseCase);
    }

    @FunctionalInterface
    public interface BoundMatchAttempt<I, O> {
        O otherwise(Function<I, O> baseCase);
    }
}
