package co.unruly.control.result;

import co.unruly.control.ThrowingLambdas;
import co.unruly.control.ThrowingLambdas.ThrowingFunction;

import java.util.function.Function;

import static co.unruly.control.result.Match.ifType;
import static co.unruly.control.result.Match.match;
import static co.unruly.control.result.Result.failure;
import static co.unruly.control.result.Result.success;
import static co.unruly.control.result.Results.flatMap;
import static co.unruly.control.result.Results.mapFailure;
import static java.util.function.Function.identity;

public class Try {

    public static <I, O, X extends Exception> Function<I, Result<O, Exception>> tryTo(ThrowingFunction<I, O, X> f) {
        return tryTo(f, identity());
    }

    public static <I, O, F, X extends Exception> Function<I, Result<O, F>> tryTo(ThrowingFunction<I, O, X> f, Function<Exception, F> exceptionHandler) {
        return flatTry(f.andThen(Result::success), exceptionHandler)::lifting;
    }

    public static <I, O, R extends Exception, X extends R> Function<I, Result<O, R>> tryTo(ThrowingFunction<I, O, X> f, Class<R> checkedType) {
        return tryTo(f, checkedType, identity())::lifting;
    }

    public static <I, O, F, R extends Exception, X extends R> Attempt<I, O, F, F> tryTo(ThrowingFunction<I, O, X> f, Class<R> checkedType, Function<R, F> exceptionHandler) {
        return flatMap(tryChecked(f, checkedType).then(mapFailure(exceptionHandler))::lifting);
    }

    public static <I, S, X extends Exception> Attempt<I, S, Exception, Exception> flatTry(ThrowingLambdas.ThrowingFunction<I, Result<S, Exception>, X> f) {
        return flatTry(f, identity());
    }

    public static <I, S, F, X extends Exception> Attempt<I, S, F, F> flatTry(ThrowingLambdas.ThrowingFunction<I, Result<S, F>, X> f, Function<Exception, F> exceptionHandler) {
        return flatMap(tryToFlat(f, exceptionHandler));
    }

    /**
     * Catches exceptions according to the provided catch clauses.
     *
     * If the exception does *not* match any of the clauses, then it is rethrown, wrapped in a RuntimeException
     */
    @SuppressWarnings("SafeVarargs")
    public static <R> Function<Exception, R> catching(EndoAttempt<R, Exception> ...catchClauses) {
        return match(catchClauses).otherwise(ex -> { throw new RuntimeException("Could not catch exception type", ex); });
    }

    private static <I, O, R extends Exception, X extends R> Attempt<I, O, Exception, R> tryChecked(ThrowingFunction<I, O, X> f, Class<R> checkedType) {
        final Function<Exception, R> castToCheckedType = Match.<Exception, R>match(
            ifType(checkedType, ex -> ex)
        ).otherwise(ex -> { throw (RuntimeException)ex;});

        return flatMap(tryTo(f, identity())).then(mapFailure(castToCheckedType));
    }


    private static <I, S, F, X extends Exception> java.util.function.Function<I, Result<S, F>> tryToFlat(ThrowingLambdas.ThrowingFunction<I, Result<S, F>, X> f, Function<Exception, F> exceptionHandler) {
        return s -> {
            try {
                return f.apply(s);
            } catch (Exception ex) {
                return failure(exceptionHandler.apply(ex));
            }
        };
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
