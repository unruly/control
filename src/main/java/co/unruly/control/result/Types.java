package co.unruly.control.result;

import static co.unruly.control.result.Results.map;
import static co.unruly.control.result.Results.mapFailure;

public interface Types {

    static <NS> SuccessConverter<NS> forSuccesses() {
        return new SuccessConverter<NS>() {
            @Override
            public <S extends NS, F> Attempt<S, NS, F, F> convert() {
                return result -> result.then(map(Types::upcast));
            }
        };
    }

    interface SuccessConverter<NS> {
        <S extends NS, F> Attempt<S, NS, F, F> convert();
    }

    static <NF> FailureConverter<NF> forFailures() {
        return new FailureConverter<NF>() {
            @Override
            public <S, T extends NF> Attempt<S, S, T, NF> convert() {
                return result -> result.then(mapFailure(Types::upcast));
            }
        };
    }

    interface FailureConverter<NF> {
        <S, F extends NF> Attempt<S, S, F, NF> convert();
    }

    static <R, T extends R> R upcast(T fv) {
        return fv;
    }
}
