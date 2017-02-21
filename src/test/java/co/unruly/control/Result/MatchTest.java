package co.unruly.control.Result;

import org.junit.Test;

import java.util.function.Function;

import static co.unruly.control.Result.Match.ifIs;
import static co.unruly.control.Result.Match.ifType;
import static co.unruly.control.Result.Match.match;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MatchTest {

    @Test
    public void canMatchOnType() {
        Function<A, String> matchByType = match(
            ifType(B.class, B::messageForB),
            ifType(C.class, C::messageForC)
        ).otherwise(A::message);

        assertThat(matchByType.apply(new A("Cheese")), is("Cheese"));
        assertThat(matchByType.apply(new B("Ketchup")), is("I'm a B and I say Ketchup"));
        assertThat(matchByType.apply(new C("Pickles")), is("I'm a C and I say Pickles"));
    }

    static class A {
        private final String msg;


        A(String msg) {
            this.msg = msg;
        }

        String message() {
            return msg;
        }
    }

    static class B extends A {

        B(String msg) {
            super(msg);
        }

        String messageForB() {
            return "I'm a B and I say " + message();
        }
    }

    static class C extends A {

        C(String msg) {
            super(msg);
        }

        String messageForC() {
            return "I'm a C and I say " + message();
        }
    }

}