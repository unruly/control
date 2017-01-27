package co.unruly.control.Result;


@FunctionalInterface
public interface ResultMapper<S, S1, F, F1> {

    Result<S1, F1> biMap(Result<S, F> r);
}
