package co.unruly.control.Result;

import org.junit.Test;

import java.io.IOException;
import java.util.function.Function;

import static co.unruly.control.Result.Match.ifType;
import static co.unruly.control.Result.Match.match;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MatchTest {

    @Test
    public void canMatchOnType() {
        FailureBiasedEndoAttempt<String, Exception> match = match(
            ifType(IOException.class, MatchTest::stringify),
            ifType(IllegalAccessException.class, MatchTest::stringifyIllegalAccess)
        );

        Function<Exception, String> fsFunction = x -> "balls";
        ResultMapper<String, Exception, String> blarg = Results.orElseGet(fsFunction);
        FailureBiasedResultMapper<String, Exception, String> flibble = match.andFinally(blarg);

        assertThat(flibble.apply(new IOException("Cheese")), is("IOException: java.io.IOException: Cheese"));
        assertThat(flibble.apply(new IllegalAccessException("Ketchup")), is("IllegalAccessException: java.lang.IllegalAccessException: Ketchup"));
        assertThat(flibble.apply(new RuntimeException("Pickles")), is("balls"));
    }

    private static String stringify(IOException ex) {
        return "IOException: " + ex;
    }
    private static String stringifyIllegalAccess(IllegalAccessException ex) {
        return "IllegalAccessException: " + ex;
    }

}