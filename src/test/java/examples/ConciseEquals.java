package examples;

import java.util.Objects;

import static co.unruly.control.casts.Equality.areEqual;

/**
 * Just a demonstration of how we can build a cleaner way to check equality
 * using Result-based casts under the hood.
 */
public class ConciseEquals {

    private final int number;
    private final String text;

    public ConciseEquals(int number, String text) {
        this.number = number;
        this.text = text;
    }

//    The best of the IntelliJ inbuilt equality templates
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ConciseEquals that = (ConciseEquals) o;
//        return number == that.number &&
//            Objects.equals(text, that.text);
//    }

    @Override
    public boolean equals(Object o) {
        return areEqual(this, o, (a, b) ->
            a.number == b.number &&
            Objects.equals(a.text, b.text)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, text);
    }
}
