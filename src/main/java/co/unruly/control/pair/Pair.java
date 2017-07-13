package co.unruly.control.pair;

import java.util.Objects;
import java.util.function.Function;

/**
 * A basic tuple type
 */
public class Pair<L, R> {

    public final L left;
    public final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    /**
     * Gets the left element. Note that Pair also supports direct member access, but this is useful when you need
     * a method reference to extract one side of the pair.
     */
    public L left() {
        return left;
    }

    /**
     * Gets the right element. Note that Pair also supports direct member access, but this is useful when you need
     * a method reference to extract one side of the pair.
     */
    public R right() {
        return right;

    }

    /**
     * Applies the given function to this pair.
     */
    public <T> T then(Function<Pair<L, R>, T> function) {
        return function.apply(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) &&
                Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
