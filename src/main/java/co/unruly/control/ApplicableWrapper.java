package co.unruly.control;

import java.util.function.Function;

/**
 * Wraps a value in an ApplicableWrapper, allowing chaining of operations and segueing into other applicable types
 * (such as Result) cleanly.
 *
 * This is most useful when not streaming over values, starting off with a value which is not a Result.
 *
 * @param <T> the type of wrapped value
 */
public class ApplicableWrapper
        <T> {

    private final T element;

    public ApplicableWrapper(T element) {
        this.element = element;
    }

    public <R> R then(Function<T, R> function) {
        return function.apply(element);
    }

    /**
     * Creates a new ApplicableWrapper wrapping the provided element.
     */
    public static <T> ApplicableWrapper<T> startWith(T element) {
        return new ApplicableWrapper<T>(element);
    }

    /**
     * When passed to ApplicableWrapper.then(), returns the wrapped value.
     */
    public static <T> Function<T, T> get() {
        return Function.identity();
    }

    /**
     * When passed to ApplicableWrapper.then(), applies the given function to the wrapped value,
     * returning a new ApplicableWrapper containing the result.
     */
    public static <I, O> Function<I, ApplicableWrapper<O>> apply(Function<I, O> function) {
        return function.andThen(ApplicableWrapper::new);
    }
}
