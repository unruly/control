package co.unruly.control.Result;

import co.unruly.control.Pair;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static co.unruly.control.Result.Result.failure;
import static co.unruly.control.Result.Result.success;
import static co.unruly.control.Result.Results.*;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


public class ResultsTest {

    @Test
    public void canCreateSuccess() {
        Result<Integer, String> shouldBeSuccess = success(5);

        assertThat(shouldBeSuccess.either(success -> true, failure -> false), is(true));
    }

    @Test
    public void canCreateFailure() {
        Result<Integer, String> shouldFail = failure("oh poop");

        assertThat(shouldFail.either(success -> true, failure -> false), is(false));
    }

    @Test
    public void canReduceResultToValue() {
        Result<Integer, String> success = success(5);
        Result<Integer, String> failure = failure("i blew up");

        assertThat(success.either(
                succ -> String.format("I got %d out of this Result", succ),
                err -> err),
            is("I got 5 out of this Result"));

        assertThat(failure.either(
                succ -> String.format("I got %d out of this Result", succ),
                err -> err),
            is("i blew up"));
    }

    @Test
    public void canDoSideEffectsOnCorrectSideForSuccess() {
        final Consumer<Integer> successCallback = mock(Consumer.class);
        final Consumer<String> failureCallback = mock(Consumer.class);

        Result<Integer, String> success = success(5);

        success
            .then(ifSuccess(successCallback))
            .then(ifFailure(failureCallback));

        verify(successCallback).accept(5);
        verifyZeroInteractions(failureCallback);
    }

    @Test
    public void canDoSideEffectsOnCorrectSideForFailure() {
        final Consumer<Integer> successCallback = mock(Consumer.class);
        final Consumer<String> failureCallback = mock(Consumer.class);

        Result<Integer, String> failure = failure("oops");

        failure
            .then(ifSuccess(successCallback))
            .then(ifFailure(failureCallback));

        verify(failureCallback).accept("oops");
        verifyZeroInteractions(successCallback);
    }

    @Test
    public void flatMapsSuccessesIntoAppropriateValues() {
        final Function<Integer, Result<Integer, String>> halve = num ->
            num % 2 == 0 ? success(num / 2) : failure("Cannot halve an odd number into an integer");

        final Result<Integer, String> six = success(6);
        final Result<Integer, String> five = success(5);
        final Result<Integer, String> failure = failure("Cannot parse number");


        assertThat(six.then(flatMap(halve)), Is.is(success(3)));
        assertThat(five.then(flatMap(halve)), Is.is(failure("Cannot halve an odd number into an integer")));
        assertThat(failure.then(flatMap(halve)), Is.is(failure("Cannot parse number")));
    }

    @Test
    public void canMapSuccesses() {
        final Result<Integer, String> six = success(6);
        final Result<Integer, String> failure = failure("Cannot parse number");

        final Result<Integer, String> twelve = six.then(map(x -> x * 2));
        final Result<Integer, String> stillFailure = failure.then(map(x -> x * 2));

        assertThat(twelve, Is.is(success(12)));
        assertThat(stillFailure, is(failure));
    }

    @Test
    public void canMapFailures() {
        final Result<Integer, String> six = success(6);
        final Result<Integer, String> failure = failure("Cannot parse number");

        final Result<Integer, String> stillSix = six.then(mapFailures(String::toLowerCase));
        final Result<Integer, String> lowerCaseFailure = failure.then(mapFailures(String::toLowerCase));

        assertThat(stillSix, Is.is(success(6)));
        assertThat(lowerCaseFailure, Is.is(failure("cannot parse number")));
    }

    @Test
    public void canMapOverExceptionThrowingMethods() {
        Result<String, String> six = success("6");
        Result<String, String> notANumber = success("NaN");


        Result<Long, String> parsedSix = six.then(tryTo(map(Long::parseLong), Exception::toString));
        Result<Long, String> parsedNaN = notANumber.then(tryTo(map(Long::parseLong), Throwable::toString));

        assertThat(parsedSix, Is.is(success(6L)));
        assertThat(parsedNaN, Is.is(failure("java.lang.NumberFormatException: For input string: \"NaN\"")));
    }

    @Test
    public void canStreamSuccesses() {
        Stream<Result<Integer, String>> results = Stream.of(success(6), success(5), failure("darnit"));

        Stream<Integer> resultStream = results.flatMap(successes());
        List<Integer> successes = resultStream.collect(toList());

        assertThat(successes, hasItems(6, 5));
    }

    @Test
    public void canStreamFailures() {
        Stream<Result<Integer, String>> results = Stream.of(success(6), success(5), failure("darnit"));

        List<String> failures = results.flatMap(failures()).collect(toList());

        assertThat(failures, hasItems("darnit"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void exampleParseAndHalveNumbers() {
        Stream<String> inputs = Stream.of("6", "5", "NaN");
        Consumer<String> failureCallback = mock(Consumer.class);

        List<Long> halvedNumbers = inputs.map(
            startingWith(String.class, String.class)
                .then(tryTo(map(Long::parseLong), Exception::toString))
                .then(flatMap(x -> x % 2 == 0 ? success(x / 2) : failure(x + " is odd")))
                .then(ifFailure(failureCallback))
        ).flatMap(successes())
         .collect(toList());

        assertThat(halvedNumbers, hasItems(3L));

        verify(failureCallback).accept("java.lang.NumberFormatException: For input string: \"NaN\"");
        verify(failureCallback).accept("5 is odd");
        verifyNoMoreInteractions(failureCallback);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void exampleSplitResults() {
        Stream<String> inputs = Stream.of("6", "5", "NaN");

        Pair<List<Long>, List<String>> halvedNumbers = inputs.map(
            startingWith(String.class, String.class)
                .then(tryTo(map(Long::parseLong), Exception::toString))
                .then(flatMap(x -> x % 2 == 0 ? success(x / 2) : failure(x + " is odd")))
        ).collect(Results.split());

        assertThat(halvedNumbers.left, hasItems(3L));
        assertThat(halvedNumbers.right, hasItems("java.lang.NumberFormatException: For input string: \"NaN\"", "5 is odd"));
    }
}