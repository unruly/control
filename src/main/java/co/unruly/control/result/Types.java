package co.unruly.control.result;

import java.util.function.Function;

import static co.unruly.control.result.Results.map;
import static co.unruly.control.result.Results.mapFailure;

public interface Types {

    static <NS> SuccessConverter<NS> forSuccesses() {
        return new SuccessConverter<NS>() {
            @Override
            public <S extends NS, F> Function<Result<S, F>, Result<NS, F>> convert() {
                return result -> result.then(map(Types::upcast));
            }
        };
    }

    interface SuccessConverter<NS> {
        <S extends NS, F> Function<Result<S, F>, Result<NS, F>> convert();
    }

    static <NF> FailureConverter<NF> forFailures() {
        return new FailureConverter<NF>() {
            @Override
            public <S, F extends NF> Function<Result<S, F>, Result<S, NF>> convert() {
                return result -> result.then(mapFailure(Types::upcast));
            }
        };
    }

    interface FailureConverter<NF> {
        <S, F extends NF> Function<Result<S, F>, Result<S, NF>> convert();
    }

    static <R, T extends R> R upcast(T fv) {
        return fv;
    }
}
