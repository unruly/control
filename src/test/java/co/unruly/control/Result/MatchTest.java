package co.unruly.control.Result;

import org.junit.Test;

import java.io.IOException;
import java.util.function.Function;

import static co.unruly.control.Result.Match.ifType;
import static co.unruly.control.Result.Match.match;
import static co.unruly.control.Result.Results.orElseGet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MatchTest {

    @Test
    public void canMatchOnType() {
        Function<Exception, String> matchException = match(
            ifType(IOException.class, MatchTest::stringify),
            ifType(IllegalAccessException.class, MatchTest::stringifyIllegalAccess)
        ).andFinally(orElseGet(x -> "balls"));

        assertThat(matchException.apply(new IOException("Cheese")), is("IOException: java.io.IOException: Cheese"));
        assertThat(matchException.apply(new IllegalAccessException("Ketchup")), is("IllegalAccessException: java.lang.IllegalAccessException: Ketchup"));
        assertThat(matchException.apply(new RuntimeException("Pickles")), is("balls"));
    }

    private static String stringify(IOException ex) {
        return "IOException: " + ex;
    }
    private static String stringifyIllegalAccess(IllegalAccessException ex) {
        return "IllegalAccessException: " + ex;
    }

    static class A {
        
    }

}