package co.unruly.control;

import co.unruly.control.pair.Pair;
import co.unruly.control.result.Result;

import java.util.List;

import static co.unruly.control.result.Resolvers.split;

public interface Lists {

    static <S, F> Result<List<S>, List<F>> successesOrFailures(List<Result<S, F>> results) {
        Pair<List<S>, List<F>> successesAndFailures = results.stream().collect(split());
        if(successesAndFailures.right.isEmpty()) {
            return Result.success(successesAndFailures.left);
        } else {
            return Result.failure(successesAndFailures.right);
        }
    }
}
