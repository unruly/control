package co.unruly.control.Result;

import java.util.function.Function;
import java.util.stream.Stream;

public class Match {

    @SafeVarargs
    public static <S, F> FailureBiasedEndoAttempt<S, F> match(EndoAttempt<S, F>... attempts) {
        return Stream.of(attempts).reduce(i -> i, EndoAttempt::then)::onResult;
    }

    public static <S, F, F1 extends F> EndoAttempt<S, F> ifType(Class<F1> type, Function<F1, S> function) {
        return x -> x.then(Results.flatMapFailures(
            failure -> Casts.cast(failure, type).then(Results.flatMap(function.andThen(Result::success)))
        ));
    }
}
