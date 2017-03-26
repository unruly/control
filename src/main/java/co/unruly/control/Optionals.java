package co.unruly.control;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Optionals {

    static <T> Stream<T> stream(Optional<T> item) {
        return either(item, Stream::of, Stream::empty);
    }

    static <T, R> R either(Optional<T> optional, Function<T, R> onPresent, Supplier<R> onAbsent) {
        return optional.map(onPresent).orElseGet(onAbsent);
    }

    static <T> void ifPresent(Optional<T> optional, Consumer<T> consume) {
        optional.ifPresent(consume);
    }

    static <T> void ifAbsent(Optional<T> optional, Runnable action) {
        if(!optional.isPresent()) {
            action.run();
        }
    }
}
