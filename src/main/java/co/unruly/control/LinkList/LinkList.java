package co.unruly.control.LinkList;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface LinkList<T> extends Iterable<T> {

    <R> R read(BiFunction<T, LinkList<T>, R> onPresent, Supplier<R> onEmpty);

    default Iterator<T> iterator() {
        return new LinkListIterator<>(this);
    }

    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
