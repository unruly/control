package co.unruly.control.matchers;

import co.unruly.control.result.Result;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class SuccessMatcher<S, F> extends TypeSafeDiagnosingMatcher<Result<S, F>> {

    private final Matcher<S> innerMatcher;

    public SuccessMatcher(Matcher<S> innerMatcher) {
        this.innerMatcher = innerMatcher;
    }

    @Override
    protected boolean matchesSafely(Result<S, F> result, Description description) {
        Boolean matches = result.either(
            innerMatcher::matches,
            failure -> false
        );

        if(!matches) {
            ResultMatchers.describeTo(result, description);
        }

        return matches;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("A Success containing ");
        innerMatcher.describeTo(description);
    }

    private void describe(Result<S, F> result, Description description) {
        result.either(
            success -> description.appendText("A Success containing " + success),
            failure -> description.appendText("A Failure containing " + failure)
        );
    }
}
