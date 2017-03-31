package co.unruly.control.pair;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Collects a stream of Pairs into a Pair of Lists.
 */
public class PairListCollector<L, R, FL, FR> implements Collector<Pair<L, R>, Pair<List<L>, List<R>>, Pair<FL, FR>> {

    private final Function<List<L>, FL> leftFinisher;
    private final Function<List<R>, FR> rightFinisher;

    public PairListCollector(Function<List<L>, FL> leftFinisher, Function<List<R>, FR> rightFinisher) {
        this.leftFinisher = leftFinisher;
        this.rightFinisher = rightFinisher;
    }

    @Override
    public Supplier<Pair<List<L>, List<R>>> supplier() {
        return () -> Pair.of(new ArrayList<L>(), new ArrayList<R>());
    }

    @Override
    public BiConsumer<Pair<List<L>, List<R>>, Pair<L, R>> accumulator() {
        return (pairs, pair) -> {
            pairs.left.add(pair.left);
            pairs.right.add(pair.right);
        };
    }

    @Override
    public BinaryOperator<Pair<List<L>, List<R>>> combiner() {
        return (first, second) -> {
            first.left.addAll(second.left);
            first.right.addAll(second.right);
            return first;
        };
    }

    @Override
    public Function<Pair<List<L>, List<R>>, Pair<FL, FR>> finisher() {
        return pair -> Pair.of(leftFinisher.apply(pair.left), rightFinisher.apply(pair.right));
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }
}
