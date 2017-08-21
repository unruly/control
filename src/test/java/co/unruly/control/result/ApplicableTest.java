package co.unruly.control.result;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static co.unruly.control.Applicable.map;
import static co.unruly.control.Applicable.startWith;
import static co.unruly.control.matchers.ResultMatchers.isFailureOf;
import static co.unruly.control.matchers.ResultMatchers.isSuccessOf;
import static co.unruly.control.result.Introducers.ifIs;
import static co.unruly.control.result.Introducers.tryTo;
import static co.unruly.control.result.Transformers.attempt;
import static co.unruly.control.result.Transformers.onFailure;
import static org.junit.Assert.assertThat;

public class ApplicableTest {

    @Test
    public void canChainSeveralOperationsBeforeOneWhichMayFail() {
        Pattern pattern = Pattern.compile("a=(\\d+);");

        Result<Integer, String> result = startWith("a=1234;")
                .then(map(pattern::matcher))
                .then(ifIs(Matcher::find, m -> m.group(1)))
                .then(onFailure(__ -> "Could not find group to match"))
                .then(attempt(tryTo(Integer::parseInt, ex -> ex.getMessage())));

        assertThat(result, isSuccessOf(1234));
    }

    @Test
    public void canChainSeveralOperationsBeforeOneWhichMayFail2() {
        Pattern pattern = Pattern.compile("a=(\\d+);");

        Result<Integer, String> result = startWith("cheeseburger")
                .then(map(pattern::matcher))
                .then(ifIs(Matcher::find, m -> m.group(1)))
                .then(onFailure(__ -> "Could not find group to match"))
                .then(attempt(tryTo(Integer::parseInt, ex -> ex.getMessage())));

        assertThat(result, isFailureOf("Could not find group to match"));
    }
}
