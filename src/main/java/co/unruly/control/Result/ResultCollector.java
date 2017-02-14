package co.unruly.control.Result;

import co.unruly.control.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

/**
 * Collects a Stream of Results into a Pair, with the left being a list of success values
 * and the right being a list of failure values.
 */
class ResultCollector<L, R>
        implements Collector<
        Result<L, R>,
        Pair<List<L>, List<R>>,
        Pair<List<L>, List<R>>> {

    @Override
    public Supplier<Pair<List<L>, List<R>>> supplier() {
        return () -> new Pair<>(new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public BiConsumer<Pair<List<L>, List<R>>, Result<L, R>> accumulator() {
        return (accumulator, Result) -> Result.either(accumulator.left::add, accumulator.right::add);
    }

    @Override
    public BinaryOperator<Pair<List<L>, List<R>>> combiner() {
        return (x, y) -> new Pair<>(
                Stream.of(x, y).flatMap(l -> l.left.stream()).collect(toList()),
                Stream.of(x, y).flatMap(l -> l.right.stream()).collect(toList())
        );
    }

    @Override
    public Function<Pair<List<L>, List<R>>, Pair<List<L>, List<R>>> finisher() {
        return pair -> new Pair<>(unmodifiableList(pair.left), unmodifiableList(pair.right));
    }


    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
