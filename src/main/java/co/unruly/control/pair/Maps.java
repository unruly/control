package co.unruly.control.pair;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Convenience methods for defining maps inline.
 */
public interface Maps {

    /**
     * Build a map from the provided key-value pairs. For example:
     * <pre>
     * {@code
     * Map<String, Int> lettersInWords = mapOf(
     *   entry("Hello", 5),
     *   entry("Goodbye", 7)
     * );
     * }
     * </pre>
     * @param entries the map entries
     * @param <K> the key type
     * @param <V> the value type
     * @return a map containing all the provided key-value pairs
     */
    @SafeVarargs
    static <K, V> Map<K, V> mapOf(Pair<K, V> ...entries) {
        return Stream.of(entries).collect(toMap());
    }

    /**
     * Collects a stream of pairs into a map
     * @param <K> the left type of the pair, interpreted as the key type
     * @param <V> the right type of the pair, interpreted as the value type
     * @return a Collector which collects a Stream of Pairs into a Map
     */
    static <K, V> Collector<Pair<K, V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(Pair::left, Pair::right);
    }

    /**
     * Creates a key-value pair.
     *
     * This is just an alias for Pair.of, that makes more sense in a map-initialisation context.
     * @param key the key
     * @param value the value
     * @param <K> the key type
     * @param <V> the value type
     * @return a key-value pair
     */
    static <K, V> Pair<K, V> entry(K key, V value) {
        return Pair.of(key, value);
    }
}
