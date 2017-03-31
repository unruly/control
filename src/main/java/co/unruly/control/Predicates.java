package co.unruly.control;

import java.util.function.Predicate;

/**
 * Created by tomj on 31/03/2017.
 */
public interface Predicates {

    /**
     * Negates a predicate: mostly useful when our predicate is a method reference or lambda where we can't
     * call negate() on it directly, or where the code reads better by having the negation at the beginning
     * rather than the end.
     */
    static <T> Predicate<T> not(Predicate<T> test) {
        return test.negate();
    }
}
