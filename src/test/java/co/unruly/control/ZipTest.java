package co.unruly.control;

import co.unruly.control.pair.Pair;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static co.unruly.control.HigherOrderFunctions.withIndices;
import static co.unruly.control.HigherOrderFunctions.zip;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class ZipTest {

    @Test
    public void zipsItemsTogether() {
        List<Pair<String, String>> pairs = zip(
            Stream.of("hello", "goodbye"),
            Stream.of("world", "cruel world"))
        .collect(Collectors.toList());

        assertThat(pairs, contains(Pair.of("hello", "world"), Pair.of("goodbye", "cruel world")));
    }

    @Test
    public void zipsItemsTogetherUsingFunction() {
        List<Integer> pairs = zip(
            Stream.of(1,2,3,4,5),
            Stream.of(1,10,100,1000,10000),
            (x, y) -> x * y)
        .collect(Collectors.toList());

        assertThat(pairs, contains(1, 20, 300, 4000, 50000));
    }

    @Test
    public void generatedStreamIsShorterOfInputStreams() {
        List<Integer> pairs = zip(
            Stream.of(1,2,3),
            Stream.of(1,10,100,1000,10000),
            (x, y) -> x * y)
        .collect(Collectors.toList());

        assertThat(pairs, contains(1, 20, 300));
    }

    @Test
    public void buildsIndexedList() {
        List<Pair<Integer, String>> indexed = withIndices(Stream.of("zero", "one", "two", "three"))
        .collect(Collectors.toList());

        assertThat(indexed, contains(Pair.of(0, "zero"), Pair.of(1, "one"), Pair.of(2, "two"), Pair.of(3, "three")));
    }
}
