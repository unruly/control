package co.unruly.control.result;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static co.unruly.control.ApplicableWrapper.apply;
import static co.unruly.control.ApplicableWrapper.startWith;
import static co.unruly.control.matchers.ResultMatchers.isFailureOf;
import static co.unruly.control.matchers.ResultMatchers.isSuccessOf;
import static co.unruly.control.result.Introducers.ifIs;
import static co.unruly.control.result.Introducers.tryTo;
import static co.unruly.control.result.Transformers.attempt;
import static co.unruly.control.result.Transformers.onFailure;
import static org.junit.Assert.assertThat;

public class ApplicableWrapperTest {

    @Test
    public void canChainSeveralOperationsBeforeOneWhichMayFail() {
        Pattern pattern = Pattern.compile("a=([^;]+);");

        Result<Integer, String> result = startWith("a=1234;")
                .then(apply(pattern::matcher))
                .then(ifIs(Matcher::find, m -> m.group(1)))
                .then(onFailure(__ -> "Could not find group to match"))
                .then(attempt(tryTo(Integer::parseInt, ex -> ex.getMessage())));

        assertThat(result, isSuccessOf(1234));
    }

    @Test
    public void canChainSeveralOperationsBeforeOneWhichMayFail2() {
        Pattern pattern = Pattern.compile("a=([^;]);");

        Result<Integer, String> result = startWith("cheeseburger")
                .then(apply(pattern::matcher))
                .then(ifIs(Matcher::find, m -> m.group(1)))
                .then(onFailure(__ -> "Could not find group to match"))
                .then(attempt(tryTo(Integer::parseInt, ex -> ex.getMessage())));

        assertThat(result, isFailureOf("Could not find group to match"));
    }

    @Test
    public void canChainSeveralOperationsBeforeOneWhichMayFail3() {
        Pattern pattern = Pattern.compile("a=([^;]);");

        Result<Integer, String> result = startWith("a=a;")
                .then(apply(pattern::matcher))
                .then(ifIs(Matcher::find, m -> m.group(1)))
                .then(onFailure(__ -> "Could not find group to match"))
                .then(attempt(tryTo(Integer::parseInt, ex -> "Parse failure: " + ex.getMessage())));

        assertThat(result, isFailureOf("Parse failure: For input string: \"a\""));
    }
}
