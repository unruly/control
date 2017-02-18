package co.unruly.control.Matchers;

import co.unruly.control.Result.Result;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

public class ResultMatchers {

    public static <S, F> Matcher<Result<S, F>> isSuccessOf(S expectedValue) {
        return isSuccessThat(equalTo(expectedValue));
    }

    public static <S, F> Matcher<Result<S, F>> isSuccessThat(Matcher<S> expectedSuccess) {
        return new SuccessMatcher<>(expectedSuccess);
    }

    public static <S, F> Matcher<Result<S, F>> isFailureOf(F expectedValue) {
        return isFailureThat(equalTo(expectedValue));
    }

    public static <S, F> Matcher<Result<S, F>> isFailureThat(Matcher<F> expectedFailure) {
        return new FailureMatcher<>(expectedFailure);
    }

    public static <S, F> void describeTo(Result<S, F> result, Description description) {
        result.either(
            success -> description.appendText("A Success containing " + success),
            failure -> description.appendText("A Failure containing " + failure)
        );
    }
}
