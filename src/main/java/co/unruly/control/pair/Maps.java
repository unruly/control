package co.unruly.control.pair;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public interface Maps {

    static <K, V> Map<K, V> mapOf(Pair<K, V> ...entries) {
        return Stream.of(entries).collect(toMap(Pair::left, Pair::right));
    }

    static <K, V> Pair<K, V> entry(K key, V value) {
        return Pair.of(key, value);
    }
}
