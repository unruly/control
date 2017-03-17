package co.unruly.control.linklist;

import org.junit.Test;

import static co.unruly.control.linklist.EmptyList.nil;
import static co.unruly.control.linklist.LinkLists.eagerConcat;
import static co.unruly.control.linklist.LinkLists.lazyConcat;
import static co.unruly.control.linklist.NonEmptyList.cons;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


public class LinkListTest {

    @Test
    public void shouldConvertLinkListToList() {
        LinkList<String> helloWorld = cons("Hello", cons("World", nil()));
        assertThat(LinkLists.toList(helloWorld), is(asList("Hello", "World")));
    }

    @Test
    public void shouldConvertListToLinkList() {
        LinkList<String> expected = cons("Hello", cons("World", nil()));
        LinkList<String> actual = LinkLists.of(asList("Hello", "World"));
        assertThat(actual, LinkListMatchers.sameContents(expected));
    }

    @Test
    public void shouldReduce() {
        LinkList<Integer> oneToSeven = LinkLists.of(asList(1,2,3,4,5,6,7));

        assertThat(LinkLists.reduce(oneToSeven, (x, y) -> x + y, 0), is(28));
    }

    @Test
    public void shouldReduceWithoutProvidingStartValue() {
        NonEmptyList<Integer> oneToThree = cons(1, cons(2, cons(3, nil())));

        assertThat(LinkLists.reduce(oneToThree, (x, y) -> x + y), is(6));
    }

    @Test
    public void canConcatenateTwoLists() {
        LinkList<Integer> oneToThree = cons(1, cons(2, cons(3, nil())));
        LinkList<Integer> fourToSix = cons(4, cons(5, cons(6, nil())));

        LinkList<Integer> oneToSix = lazyConcat(oneToThree, fourToSix);

        assertThat(LinkLists.toList(oneToSix), is(asList(1,2,3,4,5,6)));
    }

    @Test
    public void canDeepConcatenateTwoLists() {
        LinkList<Integer> oneToThree = cons(1, cons(2, cons(3, nil())));
        LinkList<Integer> fourToSix = cons(4, cons(5, cons(6, nil())));

        LinkList<Integer> oneToSix = eagerConcat(oneToThree, fourToSix);

        assertThat(LinkLists.toList(oneToSix), is(asList(1,2,3,4,5,6)));
    }

    @Test
    public void concatenatingEmptyToAListGivesOriginalList() {
        LinkList<Integer> oneToThree = cons(1, cons(2, cons(3, nil())));

        LinkList<Integer> plusNil = lazyConcat(oneToThree, nil());

        assertThat(plusNil, is(sameInstance(oneToThree)));
    }

    @Test
    public void concatenatingAListToEmptyGivesOriginalList() {
        LinkList<Integer> oneToThree = cons(1, cons(2, cons(3, nil())));

        LinkList<Integer> plusNil = lazyConcat(nil(), oneToThree);

        assertThat(plusNil, is(sameInstance(oneToThree)));
    }

}
