package co.unruly.control;

import org.junit.Test;

import java.util.function.Predicate;

import static co.unruly.control.ThrowingLambdas.ThrowingPredicate.throwingRuntime;
import static org.junit.Assert.assertTrue;

public class ThrowingLambdasTest {

//    @Test
//    public void cannotCompileWhenPassingLambdaThatThrows() throws Exception {
//        assertTrue(test(2, ThrowingLambdasTest::dodgyIsEven));
//    }

    @Test
    public void canHandleThrowingMethodsWithAppropriateFunctionalInterfaceType() {
        assertTrue(tryToTest(2, ThrowingLambdasTest::dodgyIsEven));
    }

    @Test
    public void canHandleMultiThrowingMethodsWithAppropriateFunctionalInterfaceType() {
        assertTrue(tryToTest(2, ThrowingLambdasTest::veryDodgyIsEven));
    }

    @Test
    public void canConvertThrowingLambdasToNonThrowingLambdas() throws Exception {
        assertTrue(test(2, throwingRuntime(ThrowingLambdasTest::dodgyIsEven)));
    }

    @Test
    public void canConvertMultiThrowingLambdasToNonThrowingLambdas() throws Exception {
        assertTrue(test(2, throwingRuntime(ThrowingLambdasTest::veryDodgyIsEven)));
    }

    private static <T> boolean test(T item, Predicate<T> test) {
        return test.test(item);
    }

    private static <T> boolean tryToTest(T item, ThrowingLambdas.ThrowingPredicate<T, Exception> test) {
        try {
            return test.test(item);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean dodgyIsEven(int i) throws Exception {
        return i % 2 == 0;
    }

    private static boolean veryDodgyIsEven(int i) throws FirstException, SecondException {
        return i % 2 == 0;
    }

    private static class FirstException extends Exception {}
    private static class SecondException extends Exception {}
}
