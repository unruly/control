package co.unruly.control.result;

import org.junit.Test;

import java.io.IOException;
import java.util.function.Function;

import static co.unruly.control.result.Match.ifType;
import static co.unruly.control.result.Results.collapse;
import static co.unruly.control.result.Results.ifFailed;
import static co.unruly.control.result.Try.catching;
import static co.unruly.control.result.Try.tryTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
    public void canSpecialiseHandlerForCheckedExceptions() {
        Function<String, String> doSomething = tryTo(TryTest::throwsCheckedException, CustomCheckedException.class)
            .andThen(ifFailed(CustomCheckedException::specialisedMethod));

        assertThat(doSomething.apply("throw"), is("This is something only this exception can do"));
        assertThat(doSomething.apply("play nice"), is("Today, I was good"));
    }

    @Test
    public void canUseCatchingHandlerForMultipleCheckedExceptionTypes() {
        Function<String, String> doSomething = tryTo(
            TryTest::throwsCheckedException,
            catching(
                ifType(CustomCheckedException.class, CustomCheckedException::specialisedMethod),
                ifType(IOException.class, ex -> "an IO exception, boo")
            )
        ).andThen(collapse());

        assertThat(doSomething.apply("throw"), is("This is something only this exception can do"));
        assertThat(doSomething.apply("play nice"), is("Today, I was good"));
    }

    @Test(expected = RuntimeException.class)
    public void rethrowsWhenSpecialisedHandlerForCheckedExceptionsEncountersRuntimeException() {
        Function<String, String> doSomething = tryTo(TryTest::throwsCheckedException, CustomCheckedException.class)
            .andThen(ifFailed(CustomCheckedException::specialisedMethod));

        doSomething.apply("sneakyThrow");
    }


    @Test(expected = RuntimeException.class)
    public void rethrowsWhenCatchingBlockDoesntCoverExceptionType() {
        Function<String, String> doSomething = tryTo(
            TryTest::throwsCheckedException,
            catching(
                    ifType(CustomCheckedException.class, CustomCheckedException::specialisedMethod),
                    ifType(IOException.class, ex -> "an IO exception, boo")
            )
        ).andThen(collapse());

        String sneakyThrow = doSomething.apply("sneakyThrow");
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

    static class CustomCheckedException extends Exception {

        public CustomCheckedException(String message) {
            super(message);
        }

        public String specialisedMethod() {
            return "This is something only this exception can do";
        }
    }
}
