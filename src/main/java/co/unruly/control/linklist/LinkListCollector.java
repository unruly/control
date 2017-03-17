package co.unruly.control.linklist;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static co.unruly.control.HigherOrderFunctions.flip;
import static co.unruly.control.linklist.EmptyList.nil;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toCollection;


public class LinkListCollector<T> implements Collector<T, Deque<T>, LinkList<T>> {
    @Override
    public Supplier<Deque<T>> supplier() {
        return ArrayDeque::new;
    }

    @Override
    public BiConsumer<Deque<T>, T> accumulator() {
        return Deque::addFirst;
    }

    @Override
    public BinaryOperator<Deque<T>> combiner() {
        return (deq1, deq2) -> Stream.of(deq2, deq1).flatMap(Deque::stream).collect(toCollection(ArrayDeque::new));
    }

    @Override
    public Function<Deque<T>, LinkList<T>> finisher() {
        return deq -> deq.stream().reduce(nil(), flip(NonEmptyList::cons), LinkLists::eagerConcat);
    }

    @Override
    public Set<Characteristics> characteristics() {
        return emptySet();
    }
}
