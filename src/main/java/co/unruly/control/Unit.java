package co.unruly.control;

import java.util.function.Consumer;
import java.util.function.Function;

// Unit type, which contains only one possible first, with some handy functions to convert void methods to functions
// returning Unit for functional interface compatibility.
//
// Basically java.lang.Void but it doesn't make my eyes bleed
public enum Unit {

    UNIT;

    public static <T> Function<T, Unit> functify(Consumer<T> toVoid) {
        return x -> {
            toVoid.accept(x);
            return Unit.UNIT;
        };
    }

    public static <T> Consumer<T> voidify(Function<T, ?> function) {
        return function::apply;
    }

    public static <T> Unit noOp(T __) {
        return UNIT;
    }
}
