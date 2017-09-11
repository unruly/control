package co.unruly.control.result;

import org.junit.Test;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static co.unruly.control.result.Introducers.tryAndUnwrap;
import static co.unruly.control.result.Introducers.tryTo;
import static co.unruly.control.result.Resolvers.collapse;
import static co.unruly.control.result.Resolvers.ifFailed;
import static co.unruly.control.result.Transformers.onFailure;
import static co.unruly.control.result.Transformers.onSuccess;
import static co.unruly.control.result.Transformers.unwrapSuccesses;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

//import static co.unruly.control.result.Results.ifFailed;

public class TryTest {

    @Test
    public void canHandleRuntimeExceptions() {
        Function<String, String> doSomething = tryTo(TryTest::throwsRuntimeException)
            .andThen(ifFailed(Exception::getMessage));

        assertThat(doSomething.apply("throw"), is("This is a naughty method"));
        assertThat(doSomething.apply("play nice"), is("Today, I was good"));
    }

    @Test
    public void canHandleCheckedExceptions() {
        Function<String, String> doSomething = tryTo(TryTest::throwsCheckedException)
            .andThen(ifFailed(Exception::getMessage));

        assertThat(doSomething.apply("throw"), is("This is a naughty method"));
        assertThat(doSomething.apply("play nice"), is("Today, I was good"));
    }

    @Test
    public void canHandleStreamFunctionsUsingFlatTry() {
        List<String> doingStuffWithNumbers = Stream.of("1", "two", "3")
            .flatMap(tryAndUnwrap(TryTest::throwsAndMakesStream))
            .map(onSuccess(x -> String.format("Success: %s", x)))
            .map(onFailure(Exception::getMessage))
            .map(collapse())
            .collect(toList());

        assertThat(doingStuffWithNumbers, contains(
                "Success: 1",
                "For input string: \"two\"",
                "Success: 1",
                "Success: 2",
                "Success: 3"
        ));
    }

    @Test
    public void canHandleStreamFunctionsUsingTryToAndUnwrap() {
        List<String> doingStuffWithNumbers = Stream.of("1", "two", "3")
            .map(tryTo(TryTest::throwsAndMakesStream))
            .flatMap(unwrapSuccesses())
            .map(onSuccess(x -> String.format("Success: %s", x)))
            .map(onFailure(Exception::getMessage))
            .map(collapse())
            .collect(toList());

        assertThat(doingStuffWithNumbers, contains(
                "Success: 1",
                "For input string: \"two\"",
                "Success: 1",
                "Success: 2",
                "Success: 3"
        ));
    }

    private static String throwsRuntimeException(String instruction) {
        if("throw".equals(instruction)) {
            throw new RuntimeException("This is a naughty method");
        }
        return "Today, I was good";
    }

    private static String throwsCheckedException(String instruction) throws CustomCheckedException {
        if("throw".equals(instruction)) {
            throw new CustomCheckedException("This is a naughty method");
        }
        if("sneakyThrow".equals(instruction)) {
            throw new RuntimeException("I can probably get away with this");
        }
        return "Today, I was good";
    }

    private static Stream<Integer> throwsAndMakesStream(String possiblyNumber) {
        // adding one to make the range have an inclusive end
        return IntStream.range(1, Integer.parseInt(possiblyNumber) + 1).boxed();
    }

    static class CustomCheckedException extends Exception {

        public CustomCheckedException(String message) {
            super(message);
        }

        public String specialisedMethod() {
            return "This is something only this exception can do";
        }
    }
}
