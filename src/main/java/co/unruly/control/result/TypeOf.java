package co.unruly.control.result;

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

    static <S, T, F extends T> Function<Result<S, F>, Result<S, T>> using(ForFailures<T> dummy) {
        return result -> result.then(mapFailure(TypeOf::upcast));
    }

    static <T, F, S extends T> Function<Result<S, F>, Result<T, F>> using(ForSuccesses<T> dummy) {
        return result -> result.then(map(TypeOf::upcast));
    }

    // we don't use the return value - all this does is provide type context
    static <T> ForFailures<T> forFailures() {
        return null;
    }

    // we don't use the return value - all this does is provide type context
    static <T> ForSuccesses<T> forSuccesses() {
        return null;
    }

    // this just does some magic to ensure our upcast is safe, and convert generics
    static <R, T extends R> R upcast(T item) {
        return item;
    }

    // this class only exists so we can differentiate the overloads of using()
    class ForFailures<T> {

    }

    // this class only exists so we can differentiate the overloads of using()
    class ForSuccesses<T> {

    }
}
