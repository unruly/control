package co.unruly.control.Result;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents the result of an operation which could fail, represented as either
 * a Success (wrapping the successful output) or a Failure (wrapping a value
 * describing how it failed).
 *
 * The interface for Result is minimal: many common operations are implemented
 * with static methods in Results.
 *
 * @param <S> The type of a success
 * @param <F> The type of a failure
 */
public abstract class Result<S, F>  {

    private Result() {
    }

    public static <S, F> Result<S, F> success(S value) {
        return new Success<>(value);
    }

    public static <S, F> Result<S, F> failure(F error) {
        return new Failure<>(error);
    }

    /**
     * Takes two functions, the first of which is executed in the case that this
     * Result is a Success, the second of which is executed in the case that it
     * is a Failure, on the wrapped value in either case.
     *
     * @param onSuccess the function to process the success value, if this is a Success
     * @param onFailure the function to process the failure value, if this is a Failure
     * @param <R> the type of the end result
     * @return The result of executing onSuccess if this result is a Success, or onFailure if it's a failure
     */
    public abstract <R> R either(Function<S, R> onSuccess, Function<F, R> onFailure);

    public <S1, F1> Result<S1, F1> andThen(ResultMapper<S, S1, F, F1> biMapper) {
        return biMapper.biMap(this);
    }

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
