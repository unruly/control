package co.unruly.control.result;

import org.junit.Test;

import java.util.function.Function;

import static co.unruly.control.result.Introducers.tryTo;
import static co.unruly.control.result.Resolvers.ifFailed;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
