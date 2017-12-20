package co.unruly.control;

import co.unruly.control.pair.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.iterate;

public interface HigherOrderFunctions {

    /**
     * Takes a BiFunction, and reverses the order of the arguments
     */
    static <A, B, R> BiFunction<B, A, R> flip(BiFunction<A, B, R> f) {
        return (a, b) -> f.apply(b, a);
    }

    /**
     * Takes a list of functions (which take and return the same type) and composes
     * them into a single function, applying the provided functions in order
     */
    static <T> Function<T, T> compose(Function<T, T>... functions) {
        return compose(Stream.of(functions));
    }

    /**
     * Takes a Stream of functions (which take and return the same type) and composes
     * them into a single function, applying the provided functions in order
     */
    static <T> Function<T, T> compose(Stream<Function<T, T>> functions) {
        return functions.reduce(identity(), Function::andThen);
    }

    /**
     * Takes a list of predicates and composes them into a single predicate, which
     * passes when all passed-in predicates pass
     */
    static <T> Predicate<T> compose(Predicate<T>... functions) {
        return Stream.of(functions).reduce(__ -> true, Predicate::and);
    }

    /**
     * Turns a Consumer into a Function which applies the consumer and returns the input
     */
    static <T> Function<T, T> peek(Consumer<T> action) {
        return t -> {
            action.accept(t);
            return t;
        };
    }

    static <T> Stream<Pair<Integer, T>> withIndices(Stream<T> items) {
        return zip(iterate(0, x -> x + 1), items);
    }

    static <A, B> Stream<Pair<A, B>> zip(Stream<A> a, Stream<B> b) {
        return zip(a, b, Pair::of);
    }

    /**
     * Zips two streams together using the zipper function, resulting in a single stream of
     * items from each stream combined using the provided function.
     *
     * The resultant stream will have the length of the shorter of the two input streams.
     *
     * Sourced from https://stackoverflow.com/questions/17640754/zipping-streams-using-jdk8-with-lambda-java-util-stream-streams-zip
     */
    static <A , B, C> Stream<C> zip(Stream<A> a, Stream<B> b, BiFunction<A, B, C> zipper) {
        Objects.requireNonNull(zipper);
        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(a).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(b).spliterator();

        // Zipping looses DISTINCT and SORTED characteristics
        int characteristics = aSpliterator.characteristics() & bSpliterator.characteristics() &
            ~(Spliterator.DISTINCT | Spliterator.SORTED);

        long zipSize = ((characteristics & Spliterator.SIZED) != 0)
            ? Math.min(aSpliterator.getExactSizeIfKnown(), bSpliterator.getExactSizeIfKnown())
            : -1;

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
        Iterator<C> cIterator = new Iterator<C>() {
            @Override
            public boolean hasNext() {
                return aIterator.hasNext() && bIterator.hasNext();
            }

            @Override
            public C next() {
                return zipper.apply(aIterator.next(), bIterator.next());
            }
        };

        Spliterator<C> split = Spliterators.spliterator(cIterator, zipSize, characteristics);
        return (a.isParallel() || b.isParallel())
            ? StreamSupport.stream(split, true)
            : StreamSupport.stream(split, false);
    }

    /**
     * Takes two lists, and returns a list of pairs forming the Cartesian product of those lists.
     */
    static <A, B> List<Pair<A, B>> pairs(List<A> as, List<B> bs) {
        return as.stream().flatMap(a -> bs.stream().map(b -> Pair.of(a, b))).collect(toList());
    }

    /**
     * Takes a value, and returns that same value, upcast to a suitable type. Inference is our friend here.
     */
    static <R, T extends R> R upcast(T fv) {
        return fv;
    }
}
