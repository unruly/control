package co.unruly.control.Result;

import co.unruly.control.Pair;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static co.unruly.control.Result.Results.*;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


public class ResultTest {

    @Test
    public void canCreateSuccess() {
        Result<Integer, String> shouldBeSuccess = Result.success(5);

        assertThat(succeeded(shouldBeSuccess), is(true));
    }

    @Test
    public void canCreateFailure() {
        Result<Integer, String> shouldBeSuccess = Result.failure("oh poop");

        assertThat(succeeded(shouldBeSuccess), is(false));
    }

    @Test
    public void canReduceResultToValue() {
        Result<Integer, String> success = Result.success(5);
        Result<Integer, String> failure = Result.failure("i blew up");

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
        final Consumer<Integer> onSuccess = mock(Consumer.class);
        final Consumer<String> onFailure = mock(Consumer.class);

        Result<Integer, String> success = Result.success(5);

        onSuccess(success, onSuccess);
        onFailure(success, onFailure);

        verify(onSuccess).accept(5);
        verifyZeroInteractions(onFailure);
    }

    @Test
    public void canDoSideEffectsOnCorrectSideForFailure() {
        final Consumer<Integer> onSuccess = mock(Consumer.class);
        final Consumer<String> onFailure = mock(Consumer.class);

        Result<Integer, String> failure = Result.failure("oops");

        onSuccess(failure, onSuccess);
        onFailure(failure, onFailure);

        verify(onFailure).accept("oops");
        verifyZeroInteractions(onSuccess);
    }

    @Test
    public void flatMapsSuccessesIntoAppropriateValues() {
        final Function<Integer, Result<Integer, String>> halve = num ->
            num % 2 == 0 ? Result.success(num / 2) : Result.failure("Cannot halve an odd number into an integer");

        final Result<Integer, String> six = Result.success(6);
        final Result<Integer, String> five = Result.success(5);
        final Result<Integer, String> failure = Result.failure("Cannot parse number");

        assertThat(flatMap(six, halve), Is.is(Result.success(3)));
        assertThat(flatMap(five, halve), Is.is(Result.failure("Cannot halve an odd number into an integer")));
        assertThat(flatMap(failure, halve), Is.is(Result.failure("Cannot parse number")));
    }

    @Test
    public void canMapSuccesses() {
        final Result<Integer, String> six = Result.success(6);
        final Result<Integer, String> failure = Result.failure("Cannot parse number");

        final Result<Integer, String> twelve = map(six, x -> x * 2);
        final Result<Integer, String> stillFailure = map(failure, x -> x * 2);

        assertThat(twelve, Is.is(Result.success(12)));
        assertThat(stillFailure, is(failure));
    }

    @Test
    public void canMapFailures() {
        final Result<Integer, String> six = Result.success(6);
        final Result<Integer, String> failure = Result.failure("Cannot parse number");

        final Result<Integer, String> stillSix = mapFailures(six, String::toLowerCase);
        final Result<Integer, String> lowerCaseFailure = mapFailures(failure, String::toLowerCase);

        assertThat(stillSix, Is.is(Result.success(6)));
        assertThat(lowerCaseFailure, Is.is(Result.failure("cannot parse number")));
    }

    @Test
    public void canMapOverExceptionThrowingMethods() {
        Result<String, String> six = Result.success("6");
        Result<String, String> notANumber = Result.success("NaN");

        Result<Long, String> parsedSix = tryMap(six, Long::parseLong, Throwable::toString);
        Result<Long, String> parsedNaN = tryMap(notANumber, Long::parseLong, Throwable::toString);

        assertThat(parsedSix, Is.is(Result.success(6L)));
        assertThat(parsedNaN, Is.is(Result.failure("java.lang.NumberFormatException: For input string: \"NaN\"")));
    }

    @Test
    public void canStreamSuccesses() {
        Stream<Result<Integer, String>> results = Stream.of(Result.success(6), Result.success(5), Result.failure("darnit"));

        List<Integer> successes = results.flatMap(Results::successes).collect(toList());

        assertThat(successes, hasItems(6, 5));
    }

    @Test
    public void canStreamFailures() {
        Stream<Result<Integer, String>> results = Stream.of(Result.success(6), Result.success(5), Result.failure("darnit"));

        List<String> failures = results.flatMap(Results::failures).collect(toList());

        assertThat(failures, hasItems("darnit"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void exampleParseAndHalveNumbers() {
        Stream<String> inputs = Stream.of("6", "5", "NaN");
        Consumer<String> failureCallback = mock(Consumer.class);

        List<Long> halvedNumbers = inputs.map(Result::<String, String>success)
              .map(str -> tryMap(str, Long::parseLong, Throwable::toString))
              .map(num -> flatMap(num, x -> x % 2 == 0 ? Result.success(x/2) : Result.failure(x + " is odd")))
              .peek(num -> onFailure(num, failureCallback))
              .flatMap(Results::successes)
              .collect(Collectors.toList());

        assertThat(halvedNumbers, hasItems(3L));

        verify(failureCallback).accept("java.lang.NumberFormatException: For input string: \"NaN\"");
        verify(failureCallback).accept("5 is odd");
        verifyNoMoreInteractions(failureCallback);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void exampleSplitResults() {
        Stream<String> inputs = Stream.of("6", "5", "NaN");

        Pair<List<Long>, List<String>> halvedNumbers = inputs.map(Result::<String, String>success)
                .map(str -> tryMap(str, Long::parseLong, Throwable::toString))
                .map(num -> flatMap(num, x -> x % 2 == 0 ? Result.success(x/2) : Result.failure(x + " is odd")))
                .collect(Results.split());

        assertThat(halvedNumbers.left, hasItems(3L));
        assertThat(halvedNumbers.right, hasItems("java.lang.NumberFormatException: For input string: \"NaN\"", "5 is odd"));
    }
}