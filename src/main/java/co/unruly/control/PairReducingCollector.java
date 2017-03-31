package co.unruly.control;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * A collector which performs a reduction on a stream of Pairs. This is provided as an alternative to reduce()
 * as there is no concept of a Reducer in the streams library, but this does act as a reducer, retaining only
 * the reduced value as we reduce over the stream (as opposed to building up a collection, then reducing over that).
 */
public class PairReducingCollector<L, R> implements Collector<Pair<L, R>, PairReducingCollector.MutablePair<L, R>, Pair<L, R>> {

    private final L leftIdentity;
    private final R rightIdentity;
    private final BinaryOperator<L> leftReducer;
    private final BinaryOperator<R> rightReducer;

    public PairReducingCollector(L leftIdentity, R rightIdentity, BinaryOperator<L> leftReducer, BinaryOperator<R> rightReducer) {
        this.leftIdentity = leftIdentity;
        this.rightIdentity = rightIdentity;
        this.leftReducer = leftReducer;
        this.rightReducer = rightReducer;
    }

    @Override
    public Supplier<MutablePair<L, R>> supplier() {
        return () -> new MutablePair<>(leftIdentity, rightIdentity);
    }

    @Override
    public BiConsumer<MutablePair<L, R>, Pair<L, R>> accumulator() {
        return (acc, item) -> {
            acc.left = leftReducer.apply(acc.left, item.left);
            acc.right = rightReducer.apply(acc.right, item.right);
        };
    }

    @Override
    public BinaryOperator<MutablePair<L, R>> combiner() {
        return (acc1, acc2) -> {
            acc1.left = leftReducer.apply(acc1.left, acc2.left);
            acc1.right = rightReducer.apply(acc1.right, acc2.right);
            return acc1;
        };
    }

    @Override
    public Function<MutablePair<L, R>, Pair<L, R>> finisher() {
        return acc -> Pair.of(acc.left, acc.right);
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }

    static class MutablePair<L, R> {
        L left;
        R right;

        private MutablePair(L left, R right) {
            this.left = left;
            this.right = right;
        }
    }
}
