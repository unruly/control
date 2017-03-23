package co.unruly.control.result;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface ConsumableFunction<A, B> extends Function<A, B>, Consumer<A> {

    default void accept(final A value) {
        apply(value);
    }

}
