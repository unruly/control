package co.unruly.control;

import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collector;

/**
 * Created by tomj on 31/03/2017.
 */
public interface Pairs {

    static <OL, NL, R> Function<Pair<OL, R>, Pair<NL, R>> onLeft(Function<OL, NL> leftMapper) {
        return pair -> Pair.of(leftMapper.apply(pair.left), pair.right);
    }

    static <L, OR, NR> Function<Pair<L, OR>, Pair<L, NR>> onRight(Function<OR, NR> rightMapper) {
        return pair -> Pair.of(pair.left, rightMapper.apply(pair.right));
    }

    /**
     * Collects a Stream of Pairs into a single Pair of arrays, where a given index can be used to access the left
     * and right parts of the input pairs respectively.
     */
    static <L, R> Collector<Pair<L, R>, Pair<List<L>, List<R>>, Pair<List<L>, List<R>>> toParallelLists() {
        return using(Collections::unmodifiableList, Collections::unmodifiableList);
    }

    /**
     * Collects a Stream of Pairs into a single Pair of arrays, where a given index can be used to access the left
     * and right parts of the input pairs respectively.
     */
    static <L, R> Collector<Pair<L, R>, Pair<List<L>, List<R>>, Pair<L[], R[]>> toArrays(IntFunction<L[]> leftArrayConstructor, IntFunction<R[]> rightArrayConstructor) {
        return using(
                left -> left.stream().toArray(leftArrayConstructor),
                right -> right.stream().toArray(rightArrayConstructor)
        );
    }

    /**
     * Reduces a stream of pairs to a single pair, using the provided identities and reducer functions
     */
    static <L, R> PairReducingCollector<L, R> reducing(
            L leftIdentity, BinaryOperator<L> leftReducer,
            R rightIdentity, BinaryOperator<R> rightReducer) {
        return new PairReducingCollector<>(leftIdentity, rightIdentity, leftReducer, rightReducer);
    }


    static <L, R, FL, FR> Collector<Pair<L, R>, Pair<List<L>, List<R>>, Pair<FL, FR>> using(
            Function<List<L>, FL> leftFinisher,
            Function<List<R>, FR> rightFinisher) {
        return new PairListCollector<>(leftFinisher, rightFinisher);
    }
}
