package co.unruly.control.Result;

import java.util.function.Function;
import java.util.stream.Stream;

import static co.unruly.control.Result.Casts.cast;
import static co.unruly.control.Result.Results.flatMap;
import static co.unruly.control.Result.Results.flatMapFailure;

public class Match {

    @SafeVarargs
    public static <S, F> Attempt<S, F, F, S> match(EndoAttempt<F, S>... potentialMatches) {
        return Results.<S, F>invert().then(Stream.of(potentialMatches).reduce(i -> i, EndoAttempt::then));
    }

    public static <S, F, F1 extends F> EndoAttempt<S, F> ifType(Class<F1> type, Function<F1, S> function) {
        return x -> x.then(flatMapFailure(
            failure -> cast(failure, type).then(flatMap(function.andThen(Result::success)))
        ));
    }
}
