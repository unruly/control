package co.unruly.control.linklist;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class EmptyList<T> implements LinkList<T> {

    private static final EmptyList instance = new EmptyList();

    private EmptyList() { }

    @SuppressWarnings("unchecked")
    public static <T> LinkList<T> nil() {
        return (LinkList<T>) instance;
    }

    @Override
    public <R> R read(BiFunction<T, LinkList<T>, R> onPresent, Supplier<R> onEmpty) {
        return onEmpty.get();
    }

    @Override
    public String toString() {
        return "nil";
    }
}
