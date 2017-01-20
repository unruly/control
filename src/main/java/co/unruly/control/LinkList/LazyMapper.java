package co.unruly.control.LinkList;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class LazyMapper<I, O> implements LinkList<O> {

    private final LinkList<I> toMap;
    private final Function<I, O> mappingFunction;

    public LazyMapper(LinkList<I> toMap, Function<I, O> mappingFunction) {
        this.toMap = toMap;
        this.mappingFunction = mappingFunction;
    }

    @Override
    public <R> R read(BiFunction<O, LinkList<O>, R> onPresent, Supplier<R> onEmpty) {
        return toMap.read(
                (x, xs) -> onPresent.apply(mappingFunction.apply(x), new LazyMapper<>(xs, mappingFunction)),
                onEmpty
        );
    }
}
