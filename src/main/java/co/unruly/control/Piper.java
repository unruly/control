package co.unruly.control;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Wraps a value in a Piper, allowing chaining of operations and segueing into other applicable types
 * (such as Result) cleanly.
 *
 * This is most useful when not streaming over values, starting off with a value which is not a Result.
 *
 * @param <T> the type of wrapped value
 */
public class Piper<T> {

    private final T element;

    public Piper(T element) {
        this.element = element;
    }

    /**
     * Applies the function to the piped value, returning a new pipe containing that value.
     */
    public <R> Piper<R> then(Function<T, R> function) {
        return new Piper<>(function.apply(element));
    }

    /**
     * Applies the consumer to the current value of the piped value, returning a pipe containing
     * that value.
     */
    public Piper<T> peek(Consumer<T> consumer) {
        return then(HigherOrderFunctions.peek(consumer));
    }

    /**
     * Returns the final result of the piped value, with all the piped functions applied.
     */
    public T resolve() {
        return element;
    }

    /**
     * Returns the final result of the piped value, with all the piped functions applied.
     */
    public <R> R resolveWith(Function<T, R> f) {
        return f.apply(element);
    }

    /**
     * Creates a new Piper wrapping the provided element.
     */
    public static <T> Piper<T> pipe(T element) {
        return new Piper<T>(element);
    }

}
