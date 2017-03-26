package co.unruly.control.result;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A Consumer which can also be used where a Function is required, returning its input value
 */
@FunctionalInterface
public interface ConsumableFunction<A> extends Function<A, A>, Consumer<A> {

    default A apply(final A value) {
        accept(value);
        return value;
    }
}
