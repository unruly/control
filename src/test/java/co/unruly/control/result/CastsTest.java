package co.unruly.control.result;

import org.junit.Test;

import static co.unruly.control.HigherOrderFunctions.with;
import static co.unruly.control.matchers.ResultMatchers.isFailureOf;
import static co.unruly.control.matchers.ResultMatchers.isSuccessOf;
import static co.unruly.control.result.Introducers.castTo;
import static org.junit.Assert.assertThat;

public class CastsTest {

    @Test
    public void castingToCorrectTypeYieldsSuccess() {
        final Object helloWorld = "Hello World";

        Result<String, Object> cast = with(helloWorld, castTo(String.class));

        assertThat(cast, isSuccessOf("Hello World"));
    }

    @Test
    public void castingToIncorrectTypeYieldsFailure() {
        final Object helloWorld = "Hello World";

        Result<Integer, Object> cast = with(helloWorld, castTo(Integer.class));

        assertThat(cast, isFailureOf("Hello World"));
    }
}
