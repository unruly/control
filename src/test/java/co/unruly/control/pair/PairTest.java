package co.unruly.control.pair;

import co.unruly.control.pair.Pair;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static co.unruly.control.pair.Maps.entry;
import static co.unruly.control.pair.Maps.mapOf;
import static co.unruly.control.pair.Pairs.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PairTest {

    @Test
    public void canTransformStreamOfPairs() {
        List<Pair<Integer, String>> transformedPairs = Stream.of(Pair.of(2, "hello"), Pair.of(4, "goodbye"))
                .map(onLeft(x -> x * 3))
                .map(onRight(String::toUpperCase))
                .collect(toList());

        assertThat(transformedPairs, CoreMatchers.hasItems(Pair.of(6, "HELLO"), Pair.of(12, "GOODBYE")));
    }

    @Test
    public void canTransformAnIndividualPair() {
        Pair<Integer, String> transformedPair = Pair.of(2, "hello")
                .then(onLeft(x -> x * 3))
                .then(onRight(String::toUpperCase));

        assertThat(transformedPair, is(Pair.of(6, "HELLO")));
    }

    @Test
    public void canCollectToParallelLists() {
        Pair<List<Integer>, List<String>> parallelLists = Stream.of(Pair.of(2, "hello"), Pair.of(4, "goodbye"))
                .collect(toParallelLists());

        assertThat(parallelLists, is(Pair.of(asList(2, 4), asList("hello", "goodbye"))));
    }

    @Test
    public void canCollectToParallelArrays() {
        Pair<Integer[], String[]> parallelArrays = Stream.of(Pair.of(2, "hello"), Pair.of(4, "goodbye"))
                .collect(toArrays(Integer[]::new, String[]::new));

        assertThat(asList(parallelArrays.left), is(asList(2, 4)));
        assertThat(asList(parallelArrays.right), is(asList("hello", "goodbye")));
    }

    @Test
    public void canReduceAStreamOfPairs() {
        Pair<Integer, String> reduced = Stream.of(Pair.of(2, "hello"), Pair.of(4, "goodbye"))
                .collect(reducing(
                        0, (x, y) -> x + y,
                        "", String::concat
                ));

        assertThat(reduced, is(Pair.of(6, "hellogoodbye")));
    }

    @Test
    public void canCreateMaps() {

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("hello", "world");
        expectedMap.put("six of one", "half a dozen of the other");

        Map<String, String> actualMap = mapOf(
            entry("hello", "world"),
            entry("six of one", "half a dozen of the other")
        );

        assertThat(actualMap, is(expectedMap));
    }

}