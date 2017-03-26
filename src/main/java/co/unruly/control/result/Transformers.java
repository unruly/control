package co.unruly.control.result;

import co.unruly.control.ConsumableFunction;
import co.unruly.control.HigherOrderFunctions;
import co.unruly.control.ThrowingLambdas;

import java.util.function.Consumer;
import java.util.function.Function;

import static co.unruly.control.HigherOrderFunctions.peek;
import static co.unruly.control.result.Introducers.tryTo;

/**
 * A collection of functions which take a Result and return a Result.
 */
public interface Transformers {

    /**
     * Returns a function which takes a Result and, if it's a success, applies the mapping value to that
     * success, otherwise returning the original failure.
     */
    static <IS, OS, F> Function<Result<IS, F>, Result<OS, F>> onSuccess(Function<IS, OS> mappingFunction) {
        return attempt(mappingFunction.andThen(Result::success));
    }

    /**
     * Returns a Consumer which takes a Result and, if it's a Success, passes it to the provided consumer.
     */
    static <S, F> ConsumableFunction<Result<S, F>> onSuccessDo(Consumer<S> consumer) {
        return r -> r.either(peek(consumer).andThen(Result::success), Result::failure);
    }

    /**
     * Returns a function which takes a Result with an Exception failure type and, if it's a success, applies
     * the mapping value to that success, returning a new success unless an exception is thrown, when it
     * returns a failure of that exception. If the input was a failure, it returns that failure.
     */
    static <IS, OS, X extends Exception> Function<Result<IS, Exception>, Result<OS, Exception>> onSuccessTry(
        ThrowingLambdas.ThrowingFunction<IS, OS, X> throwingFunction
    ) {
        return attempt(tryTo(throwingFunction));
    }


    /**
     * Returns a function which takes a Result and, if it's a success, applies the mapping value
     * to that success, returning a new success unless an exception is thrown, when it
     * returns a failure of the exception-mapper applied to the exception.
     * If the input was a failure, it returns that failure.
     */
    static <IS, OS, F, X extends Exception> Function<Result<IS, F>, Result<OS, F>> onSuccessTry(
        ThrowingLambdas.ThrowingFunction<IS, OS, X> throwingFunction,
        Function<Exception, F> exceptionMapper
    ) {
        return attempt(tryTo(throwingFunction).andThen(onFailure(exceptionMapper)));
    }

    /**
     * Returns a function which takes a Result and, if it's a success, applies the provided function
     * to that success - generating a new Result - and returns that Result. Otherwise, returns the
     * original failure.
     */
    static <IS, OS, F, OF extends F> Function<Result<IS, F>, Result<OS, F>> attempt(Function<IS, Result<OS, OF>> mappingFunction) {
        return r -> r.either(mappingFunction.andThen(onFailure((Function<OF, F>) (fv) -> HigherOrderFunctions.upcast(fv))), Result::failure);
    }

    /**
     * Returns a function which takes a Result and, if it's a failure, applies the provided function
     * to that failure. Otherwise, returns the original success.
     */
    static <S, IF, OF> Function<Result<S, IF>, Result<S, OF>> onFailure(Function<IF, OF> mappingFunction) {
        return attemptRecovery(mappingFunction.andThen(Result::failure));
    }

    /**
     * Returns a consumer which takes a Result and, if it's a failure, passes it to the provided consumer
     */
    static <S, F> ConsumableFunction<Result<S, F>> onFailureDo(Consumer<F> consumer) {
        return r -> r.either(Result::success, peek(consumer).andThen(Result::failure));
    }

    /**
     * Returns a function which takes a Result and, if it's a failure, applies the provided function to
     * that failure - generating a new Result - and returns that Result. Otherwise, return the original
     * success.
     */
    static <S, IF, OF, OS extends S> Function<Result<S, IF>, Result<S, OF>> attemptRecovery(Function<IF, Result<OS, OF>> recoveryFunction) {
        return r -> r.either(Result::success, recoveryFunction.andThen(onSuccess((Function<OS, S>) (fv) -> HigherOrderFunctions.upcast(fv))));
    }

    /**
     * Returns a function which takes a Result, and converts failures to successes and vice versa.
     */
    static <S, F> Function<Result<S, F>, Result<F, S>> invert() {
        return r -> r.either(Result::failure, Result::success);
    }
}
