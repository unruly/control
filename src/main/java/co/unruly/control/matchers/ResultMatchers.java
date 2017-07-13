package co.unruly.control.matchers;

import co.unruly.control.result.Result;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Hamcrest matchers for Result types
 */
public class ResultMatchers {

    /**
     * Matches if the received value is a Success containing the specified value
     */
    public static <S, F> Matcher<Result<S, F>> isSuccessOf(S expectedValue) {
        return isSuccessThat(equalTo(expectedValue));
    }

    /**
     * Matches if the received value is a Success matching the specified value
     */
    public static <S, F> Matcher<Result<S, F>> isSuccessThat(Matcher<S> expectedSuccess) {
        return new SuccessMatcher<>(expectedSuccess);
    }

    /**
     * Matches if the received value is a Failure containing the specified value
     */
    public static <S, F> Matcher<Result<S, F>> isFailureOf(F expectedValue) {
        return isFailureThat(equalTo(expectedValue));
    }

    /**
     * Matches if the received value is a Failure matching the specified value
     */
    public static <S, F> Matcher<Result<S, F>> isFailureThat(Matcher<F> expectedFailure) {
        return new FailureMatcher<>(expectedFailure);
    }

    static <S, F> void describeTo(Result<S, F> result, Description description) {
        result.either(
            success -> description.appendText("A Success containing " + success),
            failure -> description.appendText("A Failure containing " + failure)
        );
    }
}
