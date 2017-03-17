package co.unruly.control.linklist;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;

public class LinkListMatchers {

    public static <T> Matcher<LinkList<T>> sameContents(LinkList<T> expected) {
        return new CustomTypeSafeMatcher<LinkList<T>>("List contents mismatch") {
            @Override
            protected boolean matchesSafely(LinkList<T> actual) {
                return LinkLists.listsEqual(actual, expected);
            }
        };
    }
}
