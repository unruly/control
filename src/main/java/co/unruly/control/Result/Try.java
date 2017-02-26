package co.unruly.control.Result;

import co.unruly.control.ThrowingLambdas.ThrowingFunction;

import java.util.function.Function;

import static co.unruly.control.Result.Match.ifType;
import static co.unruly.control.Result.Result.failure;
import static co.unruly.control.Result.Result.success;
import static co.unruly.control.Result.Results.flatMap;
import static co.unruly.control.Result.Results.mapFailure;
import static java.util.function.Function.identity;

public class Try {

    public static <I, O, X extends Exception> Attempt<I, O, Exception, Exception> tryTo(ThrowingFunction<I, O, X> f) {
        return flatMap(tryToCall(f, identity()));
    }

    public static <I, O, F, X extends Exception> Attempt<I, O, F, F> tryTo(ThrowingFunction<I, O, X> f, Function<Exception, F> exceptionHandler) {
        return flatMap(tryToCall(f, exceptionHandler));
    }

    public static <I, O, R extends Exception, X extends R> Attempt<I, O, R, R> tryTo(ThrowingFunction<I, O, X> f, Class<R> checkedType) {
        return flatMap(tryChecked(f, checkedType));
    }

    public static <I, O, F, R extends Exception, X extends R> Attempt<I, O, F, F> tryTo(ThrowingFunction<I, O, X> f, Class<R> checkedType, Function<R, F> exceptionHandler) {
        return flatMap(tryChecked(f, checkedType).then(mapFailure(exceptionHandler)));
    }

    private static <I, O, R extends Exception, X extends R> Attempt<I, O, Exception, R> tryChecked(ThrowingFunction<I, O, X> f, Class<R> checkedType) {
        final Function<Exception, R> castToCheckedType = Match.<Exception, R>match(
            ifType(checkedType, ex -> ex)
        ).otherwise(ex -> { throw (RuntimeException)ex;});

        return tryTo(f, identity()).then(mapFailure(castToCheckedType));
    }

    private static <I, O, F, X extends Exception> Function<I, Result<O, F>> tryToCall(ThrowingFunction<I, O, X> f, Function<Exception, F> exceptionHandler) {
        return s -> {
            try {
                return success(f.apply(s));
            } catch (Exception ex) {
                return failure(exceptionHandler.apply(ex));
            }
        };
    }
}
