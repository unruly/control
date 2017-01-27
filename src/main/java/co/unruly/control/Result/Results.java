package co.unruly.control.Result;

import co.unruly.control.Pair;
import co.unruly.control.Unit;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static co.unruly.control.Result.Result.failure;
import static co.unruly.control.Result.Result.success;

public final class Results {

    public static <S, F> void onSuccess(Result<S, F> result, Consumer<S> onSuccess) {
        result.either(Unit.functify(onSuccess), Unit::noOp);
    }

    public static <S, F> void onFailure(Result<S, F> result, Consumer<F> onFailure) {
        result.either(Unit::noOp, Unit.functify(onFailure));
    }

    public static <S, F> Stream<S> successes(Result<S, F> result) {
        return result.either(Stream::of, __ -> Stream.empty());
    }

    public static <S, F> Stream<F> failures(Result<S, F> result) {
        return result.either(__ -> Stream.empty(), Stream::of);
    }

    public static <S, S1, F> Result<S1, F> map(Result<S, F> result, Function<S, S1> f) {
        return result.either(success -> success(f.apply(success)), Result::failure);
    }

    public static <S, F, F1> Result<S, F1> mapFailures(Result<S, F> result, Function<F, F1> f) {
        return result.either(Result::success, failure -> failure(f.apply(failure)));
    }

    public static <S, S1, F> Result<S1, F> flatMap(Result<S, F> result, Function<S, Result<S1, F>> f) {
        return result.either(f, Result::failure);
    }

    public static <S, S1, F> Result<S1, F> tryMap(Result<S, F> result, Function<S, S1> f, Function<Exception, F> exceptionHandler) {
        try {
            return Results.map(result, f);
        } catch (Exception e) {
            return failure(exceptionHandler.apply(e));
        }
    }

    public static <S, F> boolean succeeded(Result<S, F> result) {
        return result.either(__ -> true, __ -> false);
    }

    public static <S, F> Collector<Result<S, F>, Pair<List<S>, List<F>>, Pair<List<S>, List<F>>> split() {
        return new ResultCollector<>();
    }
}
