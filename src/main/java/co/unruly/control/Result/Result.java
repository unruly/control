package co.unruly.control.Result;

import java.util.Objects;
import java.util.function.Function;


public abstract class Result<S, F>  {

    private Result() {
    }

    public static <S, F> Result<S, F> success(S value) {
        return new Success<>(value);
    }

    public static <S, F> Result<S, F> failure(F error) {
        return new Failure<>(error);
    }

    public abstract <R> R either(Function<S, R> onSuccess, Function<F, R> onFailure);

    private static final class Success<L, R> extends Result<L, R> {
        private final L value;

        private Success(L value) {
            this.value = value;
        }

        @Override
        public <T> T either(Function<L, T> onSuccess, Function<R, T> onRight) {
            return onSuccess.apply(value);
        }

        @Override
        public String toString() {
            return "Success{" + value + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Success<?, ?> that = (Success<?, ?>) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    private static final class Failure<L, R> extends Result<L, R> {
        private final R value;

        private Failure(R value) {
            this.value = value;
        }

        @Override
        public <T> T either(Function<L, T> onSuccess, Function<R, T> onFailure) {
            return onFailure.apply(value);
        }

        @Override
        public String toString() {
            return "Failure{" + value + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Failure<?, ?> that = (Failure<?, ?>) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

}
