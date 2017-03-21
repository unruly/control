package co.unruly.control.result;

/**
 * An Attempt is a specialisation of ResultMapper which yields another Result.
 *
 * Attempts can be chained into each other, constructing a functional pipeline.
 */
@FunctionalInterface
public interface Attempt<S, S1, F, F1> extends ResultMapper<S, F, Result<S1, F1>> {

    /**
     * Compose two Attempts together, executing first this Attempt and then the provided Attempt.
     */
    default <S2, F2> Attempt<S, S2, F, F2> then(Attempt<S1, S2, F1, F2> f) {
        return this.andThen(f)::apply;
    }

    /**
     * Compose this Attempt with a terminal ResultMapper to yield a function which outputs a simple (non-Result)
     * value.
     */
    default <T> ResultMapper<S, F, T> andFinally(ResultMapper<S1, F1, T> terminal) {
        return this.andThen(terminal)::apply;
    }
}
