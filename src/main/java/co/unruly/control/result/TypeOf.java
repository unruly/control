package co.unruly.control.result;

import co.unruly.control.HigherOrderFunctions;

import java.util.function.Function;

import static co.unruly.control.result.Results.map;
import static co.unruly.control.result.Results.mapFailure;

/**
 * Some syntax-fu in order to get nice, readable up-casting operations on Results.
 *
 * Usage isn't totally obvious from the implementation: to upcast a success to an Animal, for example, you need:
 * <code>
 *     using(TypeOf.<Animal>forSuccesses())
 * </code>
 *
 * That'll give you a <code>Function<Result<Bear, String>, Function<Animal, String>></code> (inferring
 * the types Animal and String from context), which you can then use for mapping a Stream or use in
 * a Result then-operation chain.
 */
public interface TypeOf<T> {

    /**
     * Generalises the success type for a Result to an appropriate superclass.
     */
    static <T, F, S extends T> Function<Result<S, F>, Result<T, F>> using(ForSuccesses<T> dummy) {
        return result -> result.then(map(HigherOrderFunctions::upcast));
    }

    /**
     * Generalises the failure type for a Result to an appropriate superclass.
     */
    static <S, T, F extends T> Function<Result<S, F>, Result<S, T>> using(ForFailures<T> dummy) {
        return result -> result.then(mapFailure(HigherOrderFunctions::upcast));
    }

    // we don't use the return value - all this does is provide type context
    static <T> ForSuccesses<T> forSuccesses() {
        return null;
    }

    // we don't use the return value - all this does is provide type context
    static <T> ForFailures<T> forFailures() {
        return null;
    }

    // this class only exists so we can differentiate the overloads of using()
    // we don't even instantiate it
    class ForSuccesses<T> { }

    // this class only exists so we can differentiate the overloads of using()
    // we don't even instantiate it
    class ForFailures<T> { }
}
