package co.unruly.control.LinkList;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class NonEmptyList<T> implements LinkList<T> {
    public final T first;
    public final LinkList<T> rest;

    public NonEmptyList(T first, LinkList<T> rest) {
        this.first = first;
        this.rest = rest;
    }

    @Override
    public <R> R read(BiFunction<T, LinkList<T>, R> onPresent, Supplier<R> onEmpty) {
        return onPresent.apply(first, rest);
    }

    public static <T> NonEmptyList<T> cons(T first, LinkList<T> rest) {
        return new NonEmptyList<>(first, rest);
    }

    @Override
    public String toString() {
        return "cons(" + first.toString() + ", " + rest.toString() + ")";
    }
}
