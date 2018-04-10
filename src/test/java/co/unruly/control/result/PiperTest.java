package co.unruly.control.result;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static co.unruly.control.Piper.pipe;
import static co.unruly.control.matchers.ResultMatchers.isFailureOf;
import static co.unruly.control.matchers.ResultMatchers.isSuccessOf;
import static co.unruly.control.result.Introducers.ifIs;
import static co.unruly.control.result.Introducers.tryTo;
import static co.unruly.control.result.Transformers.attempt;
import static co.unruly.control.result.Transformers.onFailure;
import static org.junit.Assert.assertThat;

public class PiperTest {

    @Test
    public void canChainSeveralOperationsBeforeOneWhichMayFail() {
        Pattern pattern = Pattern.compile("a=([^;]+);");

        Result<Integer, String> result = pipe("a=1234;")
                .then(pattern::matcher)
                .then(ifIs(Matcher::find, m -> m.group(1)))
                .then(onFailure(__ -> "Could not find group to match"))
                .then(attempt(tryTo(Integer::parseInt, ex -> ex.getMessage())))
                .resolve();

        assertThat(result, isSuccessOf(1234));
    }

    @Test
    public void canChainSeveralOperationsBeforeOneWhichMayFail2() {
        Pattern pattern = Pattern.compile("a=([^;]);");

        Result<Integer, String> result = pipe("cheeseburger")
                .then(pattern::matcher)
                .then(ifIs(Matcher::find, m -> m.group(1)))
                .then(onFailure(__ -> "Could not find group to match"))
                .then(attempt(tryTo(Integer::parseInt, ex -> ex.getMessage())))
                .resolve();

        assertThat(result, isFailureOf("Could not find group to match"));
    }

    @Test
    public void canChainSeveralOperationsBeforeOneWhichMayFail3() {
        Pattern pattern = Pattern.compile("a=([^;]);");

        Result<Integer, String> result = pipe("a=a;")
                .then(pattern::matcher)
                .then(ifIs(Matcher::find, m -> m.group(1)))
                .then(onFailure(__ -> "Could not find group to match"))
                .then(attempt(tryTo(Integer::parseInt, ex -> "Parse failure: " + ex.getMessage())))
                .resolve();

        assertThat(result, isFailureOf("Parse failure: For input string: \"a\""));
    }
}
