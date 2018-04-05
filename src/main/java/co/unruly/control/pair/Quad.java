package co.unruly.control.pair;

import java.util.Objects;
import java.util.function.Function;

public class Quad<A, B, C, D> {

    @FunctionalInterface
    public interface QuadFunction<A, B, C, D, T> {
        T apply(A a, B b, C c, D d);
    }

    public final A first;
    public final B second;
    public final C third;
    public final D fourth;


    public Quad(A first, B second, C third, D fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public static <A, B, C, D> Quad<A, B, C, D> of(A first, B second, C third, D fourth) {
        return new Quad(first, second, third, fourth);
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    public C third() {
        return third;
    }

    public D fourth() {
        return fourth;
    }

    public <T> T then(Function<Quad<A, B, C, D>, T> function) {
        return function.apply(this);
    }

    public <T> T then(QuadFunction<A, B, C, D, T> function) {
        return function.apply(first, second, third, fourth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quad<?, ?, ?, ?> quad = (Quad<?, ?, ?, ?>) o;
        return Objects.equals(first, quad.first) &&
                Objects.equals(second, quad.second) &&
                Objects.equals(third, quad.third) &&
                Objects.equals(fourth, quad.fourth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third, fourth);
    }

    @Override
    public String toString() {
        return "Quad{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                ", fourth=" + fourth +
                '}';
    }
}
