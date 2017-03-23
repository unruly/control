package co.unruly.control.result;

import co.unruly.control.ThrowingLambdas;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static co.unruly.control.Unit.UNIT;

public interface StreamingResults {

    static <I, O, X extends Exception> Function<I, Result<O, Exception>> tryTo(ThrowingLambdas.ThrowingFunction<I, O, X> f) {
        return Try.tryTo(f);
    }

    static <IS, OS, F> Function<Result<IS, F>, Result<OS, F>> onSuccess(Function<IS, OS> function) {
        return result -> result.either(function.andThen(Result::success), Result::failure);
    }

    static <IS, OS, OF extends Exception, IF extends OF, X extends Exception> Function<Result<IS, IF>, Result<OS, Exception>> onSuccessTry(ThrowingLambdas.ThrowingFunction<IS, OS, X> function) {
        return result -> result.either(tryTo(function), Result::failure);
    }

    static <S, F> Consumer<Result<S, F>> onSuccess(Consumer<S> consumer) {
        return result -> result.either(success -> { consumer.accept(success); return UNIT; } , f -> UNIT);
    }

    static <S, F, EF extends F> Function<Result<S, F>, Result<S, F>> recoverIf(Class<EF> clazz, Function<EF, S> recoveryFunction) {
        return Match.ifType(clazz, recoveryFunction);
    }

    static <S, F, EF extends F> Function<Result<S, F>, Result<S, F>> recover(Function<Result<S, F>, Result<S, F>> recoveryFunction) {
        return recoveryFunction;
    }

    static <FS, IS extends FS, F, OS extends FS> Function<Result<IS, F>, FS> recoverAll(Function<F, OS> recovery) {
        return result -> result.either(i -> i, recovery);
    }

    static <FS, IS extends FS, F, OS extends FS> Function<Result<IS, F>, FS> recoverAll(Supplier<OS> defaultValue) {
        return recoverAll(__ -> defaultValue.get());
    }
}
