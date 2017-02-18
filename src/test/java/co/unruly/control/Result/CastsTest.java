package co.unruly.control.Result;

import org.junit.Test;

import static co.unruly.control.Matchers.ResultMatchers.isFailureOf;
import static co.unruly.control.Matchers.ResultMatchers.isSuccessOf;
import static co.unruly.control.Result.Casts.cast;
import static org.junit.Assert.assertThat;

public class CastsTest {

    @Test
    public void castingToCorrectTypeYieldsSuccess() {
        final Object helloWorld = "Hello World";

        Result<String, Object> cast = cast(helloWorld, String.class);

        assertThat(cast, isSuccessOf("Hello World"));
    }

    @Test
    public void castingToIncorrectTypeYieldsFailure() {
        final Object helloWorld = "Hello World";

        Result<Integer, Object> cast = cast(helloWorld, Integer.class);

        assertThat(cast, isFailureOf("Hello World"));
    }
}