package co.unruly.control.pair;

import co.unruly.control.pair.Quad.QuadFunction;
import co.unruly.control.pair.Triple.TriFunction;
import co.unruly.control.result.Result;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static co.unruly.control.result.Transformers.attempt;
import static co.unruly.control.result.Transformers.onSuccess;

public interface Comprehensions {

    static <L, R, T> Function<Pair<L, R>, T> onAll(BiFunction<L, R, T> f) {
        return pair -> pair.then(f);
    }

    static <A, B, C, T> Function<Triple<A, B, C>, T> onAll(TriFunction<A, B, C, T> f) {
        return triple -> triple.then(f);
    }

    static <A, B, C, D, T> Function<Quad<A, B, C, D>, T> onAll(QuadFunction<A, B, C, D, T> f) {
        return quad -> quad.then(f);
    }

    static <L, R> Optional<Pair<L, R>> allOf(Optional<L> maybeLeft, Optional<R> maybeRight) {
        return maybeLeft.flatMap(left ->
            maybeRight.map(right ->
                Pair.of(left, right)));
    }

    static <A, B, C> Optional<Triple<A, B, C>> allOf(Optional<A> maybeFirst, Optional<B> maybeSecond, Optional<C> maybeThird) {
        return maybeFirst.flatMap(first -> maybeSecond.flatMap(second -> maybeThird.map(third -> Triple.of(first, second, third))));
    }

    static <A, B, C, D> Optional<Quad<A, B, C, D>> allOf(Optional<A> maybeFirst, Optional<B> maybeSecond, Optional<C> maybeThird, Optional<D> maybeFourth) {
        return maybeFirst.flatMap(first -> maybeSecond.flatMap(second -> maybeThird.flatMap(third -> maybeFourth.map(fourth -> Quad.of(first, second, third, fourth)))));
    }

    static <F, LS, RS> Result<Pair<LS, RS>, F> allOf(Result<LS, F> left, Result<RS, F> right) {
        return left.then(attempt(l ->
            right.then(onSuccess(r ->
                Pair.of(l, r)))));
    }

    static <F, S1, S2, S3> Result<Triple<S1, S2, S3>, F> allOf(Result<S1, F> first, Result<S2, F> second, Result<S3, F> third) {
        return  first.then(attempt(firstValue ->
                    second.then(attempt(secondValue ->
                        third.then(onSuccess(thirdValue ->
                            Triple.of(firstValue, secondValue, thirdValue)
                        ))
                    ))
                ));
    }

    static <F, S1, S2, S3, S4> Result<Quad<S1, S2, S3, S4>, F> allOf(Result<S1, F> first, Result<S2, F> second, Result<S3, F> third, Result<S4, F> fourth) {
        return  first.then(attempt(firstValue ->
                    second.then(attempt(secondValue ->
                        third.then(attempt(thirdValue ->
                            fourth.then(onSuccess(fourthValue ->
                                Quad.of(firstValue, secondValue, thirdValue, fourthValue)
                            ))
                        ))
                    ))
                ));
    }


    static <F, S1, S2, SR> Function<Result<Pair<S1, S2>, F>, Result<SR, F>> ifAllSucceeded(
        BiFunction<S1, S2, SR> f
    ) {
        return onSuccess(onAll(f));
    }

    static <F, S1, S2, S3, SR> Function<Result<Triple<S1, S2, S3>, F>, Result<SR, F>> ifAllSucceeded(
        TriFunction<S1, S2, S3, SR> f
    ) {
        return onSuccess(onAll(f));
    }

    static <F, S1, S2, S3, S4, SR> Function<Result<Quad<S1, S2, S3, S4>, F>, Result<SR, F>> ifAllSucceeded(
        QuadFunction<S1, S2, S3, S4, SR> f
    ) {
        return onSuccess(onAll(f));
    }

}
