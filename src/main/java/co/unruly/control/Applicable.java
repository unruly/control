package co.unruly.control;

import java.util.function.Function;

public class Applicable<T> {

    private final T element;

    public Applicable(T element) {
        this.element = element;
    }

    public <R> R then(Function<T, R> function) {
        return function.apply(element);
    }

    public static <T> Applicable<T> startWith(T element) {
        return new Applicable<T>(element);
    }

    public static <I, O> Function<I, Applicable<O>> map(Function<I, O> function) {
        return function.andThen(Applicable::new);
    }
}
