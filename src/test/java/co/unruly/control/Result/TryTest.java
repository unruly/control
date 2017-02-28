package co.unruly.control.Result;

import co.unruly.control.ThrowingLambdas;
import org.junit.Test;

import java.io.IOException;
import java.util.function.Function;

import static co.unruly.control.Result.Match.ifType;
import static co.unruly.control.Result.Results.collapse;
import static co.unruly.control.Result.Results.orElseGet;
import static co.unruly.control.Result.Try.catching;
import static co.unruly.control.Result.Try.tryTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TryTest {

    @Test
    public void canHandleRuntimeExceptions() {
        Function<String, String> doSomething = tryTo(TryTest::throwsRuntimeException)
            .andFinally(orElseGet(Exception::getMessage));

        assertThat(doSomething.apply("throw"), is("This is a naughty method"));
        assertThat(doSomething.apply("play nice"), is("Today, I was good"));
    }

    @Test
    public void canHandleCheckedExceptions() {
        Function<String, String> doSomething = tryTo(TryTest::throwsCheckedException)
            .andFinally(orElseGet(Exception::getMessage));

        assertThat(doSomething.apply("throw"), is("This is a naughty method"));
        assertThat(doSomething.apply("play nice"), is("Today, I was good"));
    }

    @Test
    public void canSpecialiseHandlerForCheckedExceptions() {
        Function<String, String> doSomething = tryTo(TryTest::throwsCheckedException, CheckedException.class)
            .andFinally(orElseGet(CheckedException::specialisedMethod));

        assertThat(doSomething.apply("throw"), is("This is something only this exception can do"));
        assertThat(doSomething.apply("play nice"), is("Today, I was good"));
    }

    @Test
    public void canUseCatchingHandlerForMultipleCheckedExceptionTypes() {
        Function<String, String> doSomething = tryTo(
            TryTest::throwsCheckedException,
            catching(
                ifType(CheckedException.class, CheckedException::specialisedMethod),
                ifType(IOException.class, ex -> "an IO exception, boo")
            )
        ).andFinally(collapse());

        assertThat(doSomething.apply("throw"), is("This is something only this exception can do"));
        assertThat(doSomething.apply("play nice"), is("Today, I was good"));
    }

    @Test(expected = RuntimeException.class)
    public void rethrowsWhenSpecialisedHandlerForCheckedExceptionsEncountersRuntimeException() {
        Function<String, String> doSomething = tryTo(TryTest::throwsCheckedException, CheckedException.class)
            .andFinally(orElseGet(CheckedException::specialisedMethod));

        doSomething.apply("sneakyThrow");
    }

    private static String throwsRuntimeException(String instruction) {
        if("throw".equals(instruction)) {
            throw new RuntimeException("This is a naughty method");
        }
        return "Today, I was good";
    }

    private static String throwsCheckedException(String instruction) throws CheckedException {
        if("throw".equals(instruction)) {
            throw new CheckedException("This is a naughty method");
        }
        if("sneakyThrow".equals(instruction)) {
            throw new RuntimeException("I can probably get away with this");
        }
        return "Today, I was good";
    }

    static class CheckedException extends Exception {

        public CheckedException(String message) {
            super(message);
        }

        public String specialisedMethod() {
            return "This is something only this exception can do";
        }
    }
}
