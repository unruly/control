package co.unruly.control.pair;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Maps {

    @SafeVarargs
    static <K, V> Map<K, V> mapOf(Pair<K, V> ...entries) {
        return Stream.of(entries).collect(toMap());
    }

    static <K, V> Collector<Pair<K, V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(Pair::left, Pair::right);
    }

    static <K, V> Pair<K, V> entry(K key, V value) {
        return Pair.of(key, value);
    }
}
