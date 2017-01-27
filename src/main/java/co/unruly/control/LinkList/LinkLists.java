package co.unruly.control.LinkList;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class LinkLists {

    public static <T> List<T> toList(LinkList<T> list) {
        return list.stream().collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T> LinkList<T> of(T... items) {
        return of(asList(items));
    }

    public static <T> LinkList<T> of(List<T> list) {
        final ArrayList<T> reversed = new ArrayList<>(list);
        Collections.reverse(reversed);
        LinkList<T> rest = EmptyList.nil();
        for(T t : reversed) {
            rest = NonEmptyList.cons(t, rest);
        }
        return rest;
    }

    public static <T> Optional<NonEmptyList<T>> nonEmptyList(List<T> list) {
        return nonEmptyList(of(list));
    }

    public static <T> Optional<NonEmptyList<T>> nonEmptyList(LinkList<T> list) {
        return list.read(
                (x, xs) -> Optional.of(NonEmptyList.cons(x, xs)),
                Optional::empty
        );
    }

    public static <I, O> LinkList<O> lazyMap(LinkList<I> list, Function<I, O> mappingFunction) {
        return new LazyMapper<>(list, mappingFunction);
    }

    public static <T> LinkList<T> lazyConcat(LinkList<T> first, LinkList<T> second) {
        return first.read(
            (x, xs) -> second.read(
                (y, ys) -> new ConcatList<>(NonEmptyList.cons(x, xs), second),
                () -> first
            ),
            () -> second
        );
    }

    public static <T> LinkList<T> eagerConcat(LinkList<T> first, LinkList<T> second) {
        return first.read(
            (x, xs) -> NonEmptyList.cons(x, eagerConcat(xs, second)),
            () -> second
        );
    }

    public static <T, R> R reduce(LinkList<T> list, BiFunction<T, R, R> combiner, R accumulator) {
        return list.read(
                (t, ts) -> combiner.apply(t, reduce(ts, combiner, accumulator)),
                () -> accumulator
        );
    }

    public static <T> T reduce(NonEmptyList<T> list, BiFunction<T, T, T> combiner) {
        return reduce(list.rest, combiner, list.first);
    }

    public static <T> Collector<T, Deque<T>, LinkList<T>> toLinkList() {
        return new LinkListCollector<>();
    }

    public static boolean listsEqual(LinkList<?> first, LinkList<?> second) {
        return first.read(
                (x, xs) -> second.read(
                        (y, ys) -> x.equals(y) && listsEqual(xs, ys),
                        () -> false
                ),
                () -> second.read(
                        (y, ys) -> false,
                        () -> true
                )
        );
    }
}
