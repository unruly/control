package co.unruly.control.validation;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public interface ForwardingList<T>  {

    List<T> delegate();

    default int size() {
        return delegate().size();
    }

    default boolean isEmpty() {
        return delegate().isEmpty();
    }

    default boolean contains(Object o) {
        return delegate().contains(o);
    }

    default Iterator<T> iterator() {
        return delegate().iterator();
    }

    default Object[] toArray() {
        return delegate().toArray();
    }

    default <T1> T1[] toArray(T1[] a) {
        return delegate().toArray(a);
    }

    default boolean add(T t) {
        return delegate().add(t);
    }

    default boolean remove(Object o) {
        return delegate().remove(o);
    }

    default boolean containsAll(Collection<?> c) {
        return delegate().containsAll(c);
    }

    default boolean addAll(Collection<? extends T> c) {
        return delegate().addAll(c);
    }

    default boolean addAll(int index, Collection<? extends T> c) {
        return delegate().addAll(index, c);
    }

    default boolean removeAll(Collection<?> c) {
        return delegate().removeAll(c);
    }

    default boolean retainAll(Collection<?> c) {
        return delegate().retainAll(c);
    }

    default void replaceAll(UnaryOperator<T> operator) {
        delegate().replaceAll(operator);
    }

    default void sort(Comparator<? super T> c) {
        delegate().sort(c);
    }

    default void clear() {
        delegate().clear();
    }

    default T get(int index) {
        return delegate().get(index);
    }

    default T set(int index, T element) {
        return delegate().set(index, element);
    }

    default void add(int index, T element) {
        delegate().add(index, element);
    }

    default T remove(int index) {
        return delegate().remove(index);
    }

    default int indexOf(Object o) {
        return delegate().indexOf(o);
    }

    default int lastIndexOf(Object o) {
        return delegate().lastIndexOf(o);
    }

    default ListIterator<T> listIterator() {
        return delegate().listIterator();
    }

    default ListIterator<T> listIterator(int index) {
        return delegate().listIterator(index);
    }

    default List<T> subList(int fromIndex, int toIndex) {
        return delegate().subList(fromIndex, toIndex);
    }

    default Spliterator<T> spliterator() {
        return delegate().spliterator();
    }

    default boolean removeIf(Predicate<? super T> filter) {
        return delegate().removeIf(filter);
    }

    default Stream<T> stream() {
        return delegate().stream();
    }

    default Stream<T> parallelStream() {
        return delegate().parallelStream();
    }

    default void forEach(Consumer<? super T> action) {
        delegate().forEach(action);
    }
}
