package co.unruly.control.matchers;

import co.unruly.control.result.Result;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class FailureMatcher<S, F> extends TypeSafeDiagnosingMatcher<Result<S, F>> {

    private final Matcher<F> innerMatcher;

    public FailureMatcher(Matcher<F> innerMatcher) {
        this.innerMatcher = innerMatcher;
    }

    @Override
    protected boolean matchesSafely(Result<S, F> result, Description description) {
        Boolean matches = result.either(
            success -> false,
            innerMatcher::matches
        );

        if(!matches) {
            ResultMatchers.describeTo(result, description);
        }

        return matches;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("A Failure containing ");
        innerMatcher.describeTo(description);
    }
}
