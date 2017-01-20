package co.unruly.control.LinkList;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ConcatList<T> implements LinkList<T> {

    private final LinkList<T> first;
    private final LinkList<T> second;

    public ConcatList(LinkList<T> first, LinkList<T> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public <R> R read(BiFunction<T, LinkList<T>, R> onPresent, Supplier<R> onEmpty) {
        return first.read(
                (t, ts) -> onPresent.apply(t, new ConcatList<>(ts, second)),
                () -> second.read(onPresent, onEmpty)
        );
    }

    @Override
    public String toString() {
        return read(
                (x, xs) -> "cons(" + x.toString() + ", " + xs.toString() + ")",
                () -> "nil"
        );
    }
}
