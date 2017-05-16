package co.unruly.control.result;

import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;

import static co.unruly.control.result.Introducers.*;
import static co.unruly.control.result.Match.match;
import static co.unruly.control.result.Match.matchValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MatchTest {

    @Test
    public void canMatchOnTypeWithFlowTyping() {
        Function<A, String> matchByType = match(
                ifType(B.class, B::messageForB),
                ifType(C.class, C::messageForC)
        ).otherwise(A::message);

        assertThat(matchByType.apply(new A("Cheese")), is("Cheese"));
        assertThat(matchByType.apply(new B("Ketchup")), is("I'm a B and I say Ketchup"));
        assertThat(matchByType.apply(new C("Pickles")), is("I'm a C and I say Pickles"));
    }

    @Test
    public void canMatchOnValue() {
        Function<Integer, String> matchByType = match(
                ifEquals(4, x -> x + " sure looks like a 4 to me!"),
                ifEquals(7, x -> x + " looks like one of them gosh-darned 7s?")
        ).otherwise(x -> "I have no idea what a " + x + " is though...");

        assertThat(matchByType.apply(3), is("I have no idea what a 3 is though..."));
        assertThat(matchByType.apply(4), is("4 sure looks like a 4 to me!"));
        assertThat(matchByType.apply(6), is("I have no idea what a 6 is though..."));
        assertThat(matchByType.apply(7), is("7 looks like one of them gosh-darned 7s?"));
    }

    @Test
    public void canMatchOnTest() {
        Function<Integer, String> matchByType = match(
                ifIs((Integer x) -> x % 2 == 0, x -> x + ", well, that's one of those even numbers"),
                ifIs(x -> x < 0,                x -> x + " is one of those banker's negative number thingies")
        ).otherwise(x -> x + " is a regular, god-fearing number for god-fearing folks");

        assertThat(matchByType.apply(2), is("2, well, that's one of those even numbers"));
        assertThat(matchByType.apply(-6), is("-6, well, that's one of those even numbers"));
        assertThat(matchByType.apply(3), is("3 is a regular, god-fearing number for god-fearing folks"));
        assertThat(matchByType.apply(-9), is("-9 is one of those banker's negative number thingies"));
    }

    @Test
    public void canMatchOnTestPassingArgument() {
        String matchByResult = matchValue(4,
                ifIs((Integer x) -> x % 2 == 0, x -> x + ", well, that's one of those even numbers"),
                ifIs(x -> x < 0,                x -> x + " is one of those banker's negative number thingies")
        ).otherwise(x -> x + " is a regular, god-fearing number for god-fearing folks");

        assertThat(matchByResult, is("4, well, that's one of those even numbers"));
    }

    @Test
    public void canOperateOverAListOfOptionalProviders() {
        String cheese = matchValue(new Things(null, "Cheese!", "Bacon!"),
                ifPresent(Things::a),
                ifPresent(Things::b),
                ifPresent(Things::c)
        ).otherwise(__ -> "Ketchup!");

        assertThat(cheese, is("Cheese!"));
    }


    @Test
    public void usesDefaultIfNoOptionalProvidersProvideAValue() {
        String cheese = matchValue(new Things(null, null, null),
                ifPresent(Things::a),
                ifPresent(Things::b),
                ifPresent(Things::c)
        ).otherwise(__ -> "Ketchup!");

        assertThat(cheese, is("Ketchup!"));
    }

    @Test
    public void useMatchToCalculateFactorial() {
        assertThat(factorial(0), is(1));
        assertThat(factorial(1), is(1));
        assertThat(factorial(6), is(720));
    }

    @Test(expected = IllegalArgumentException.class)
    public void factorialOfNegativeNumberThrowsIllegalArgumentException() {
        factorial(-1);
    }

    private static int factorial(int number) {
        return matchValue(number,
            ifIs(n -> n < 0, n -> { throw new IllegalArgumentException("Cannot calculate factorial of a negative number"); }),
            ifEquals(0, n -> 1)
        ).otherwise(n -> n * factorial(n-1));
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

    static class Things {
        final String a;
        final String b;
        final String c;


        Things(String a, String b, String c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        Optional<String> a() {
            return Optional.ofNullable(a);
        }


        Optional<String> b() {
            return Optional.ofNullable(b);
        }


        Optional<String> c() {
            return Optional.ofNullable(c);
        }
    }

}
