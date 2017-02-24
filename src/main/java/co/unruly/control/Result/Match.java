package co.unruly.control.Result;

import co.unruly.control.Unit;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static co.unruly.control.Result.Casts.cast;
import static co.unruly.control.Result.EndoAttempt.compose;
import static co.unruly.control.Result.Results.flatMap;
import static co.unruly.control.Result.Results.flatMapFailure;
import static co.unruly.control.Result.Results.orElseGet;

public class Match {

    @SafeVarargs
    public static <I, O> MatchAttempt<I, O> match(EndoAttempt<O, I>... potentialMatchers) {
        return f -> attemptMatch(potentialMatchers).andFinally(orElseGet(f));
    }

    @SafeVarargs
    public static <I, O> BoundMatchAttempt<I, O> matchValue(I inputValue, EndoAttempt<O, I>... potentialMatchers) {
        return f -> attemptMatch(potentialMatchers).andFinally(orElseGet(f)).apply(inputValue);
    }

    @SafeVarargs
    public static <S, F> Attempt<S, F, F, S> attemptMatch(EndoAttempt<F, S>... potentialMatches) {
        return Results.<S, F>invert().then(compose(potentialMatches));
    }

    public static <S, F, F1 extends F> EndoAttempt<S, F> ifType(Class<F1> type, Function<F1, S> function) {
        return x -> x.then(flatMapFailure(
                failure -> cast(failure, type).then(flatMap(function.andThen(Result::success)))
        ));
    }

    public static <S, F> EndoAttempt<S, F> ifIs(Predicate<F> pred, Function<F, S> function) {
        return x -> x.then(flatMapFailure(
                failure -> pred.test(failure) ? Result.success(function.apply(failure)) : Result.failure(failure)
        ));
    }

    public static <S, F> EndoAttempt<S, F> ifEquals(F value, Function<F, S> function) {
        return ifIs(value::equals, function);
    }

    public static <S, F> EndoAttempt<S, F> ifPresent(Function<F, Optional<S>> successProvider) {
        return r -> r.then(Results.flatMapFailure(f -> successProvider.apply(f).map(Result::<S, F>success).orElseGet(() -> Result.failure(f))));
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
