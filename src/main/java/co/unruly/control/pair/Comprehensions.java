package co.unruly.control.pair;

import co.unruly.control.pair.Triple.TriFunction;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Comprehensions {

    static <L, R> Optional<Pair<L, R>> allOf(Optional<L> maybeLeft, Optional<R> maybeRight) {
        return maybeLeft.flatMap(left -> maybeRight.map(right -> Pair.of(left, right)));
    }

    static <L, R, T> Function<Pair<L, R>, T> onAll(BiFunction<L, R, T> f) {
        return pair -> pair.then(f);
    }


    static <A, B, C> Optional<Triple<A, B, C>> allOf(Optional<A> maybeFirst, Optional<B> maybeSecond, Optional<C> maybeThird) {
        return maybeFirst.flatMap(first -> maybeSecond.flatMap(second -> maybeThird.map(third -> Triple.of(first, second, third))));
    }

    static <A, B, C, T> Function<Triple<A, B, C>, T> onAll(TriFunction<A, B, C, T> f) {
        return triple -> triple.then(f);
    }


    static <A, B, C, D> Optional<Quad<A, B, C, D>> allOf(Optional<A> maybeFirst, Optional<B> maybeSecond, Optional<C> maybeThird, Optional<D> maybeFourth) {
        return maybeFirst.flatMap(first -> maybeSecond.flatMap(second -> maybeThird.flatMap(third -> maybeFourth.map(fourth -> Quad.of(first, second, third, fourth)))));
    }

    static <A, B, C, D, T> Function<Quad<A, B, C, D>, T> onAll(Quad.QuadFunction<A, B, C, D, T> f) {
        return quad -> quad.then(f);
    }
}
