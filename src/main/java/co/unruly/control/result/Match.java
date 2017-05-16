package co.unruly.control.result;

import java.util.function.Function;
import java.util.stream.Stream;

import static co.unruly.control.result.Resolvers.ifFailed;
import static co.unruly.control.result.Result.failure;

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
    public static <I, O> MatchAttempt<I, O> match(Function<I, Result<O, I>>... potentialMatchers) {
        return f -> Stream.of(potentialMatchers)
                .map(Transformers::attemptRecovery)
                .reduce(i -> i, Function::andThen)
                .andThen(ifFailed(f))
                .compose(Result::failure);
    }

    /**
     * Dispatches a value across the provided matchers. Note that in order to yield
     * a value, the otherwise() method must be called on the result of this function:
     * as there's no way to determine if the dispatch table is complete, a base case is
     * required.
     */
    @SafeVarargs
    public static <I, O> BoundMatchAttempt<I, O> matchValue(I inputValue, Function<I, Result<O, I>>... potentialMatchers) {
        return f -> Stream.of(potentialMatchers)
                .map(Transformers::attemptRecovery)
                .reduce(i -> i, Function::andThen)
                .andThen(ifFailed(f))
                .apply(failure(inputValue));
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
