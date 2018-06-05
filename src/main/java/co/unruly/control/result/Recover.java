package co.unruly.control.result;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A collection of functions to (conditionally) recover a failure into a success.
 */
public interface Recover {

    /**
     * Returns a function which takes an Optional value, and returns a failure of the
     * wrapped value if it was present, otherwise returns a success using the provided Supplier
     */
    static <S, F> Function<Optional<F>, Result<S, F>> whenAbsent(Supplier<S> onEmpty) {
        return maybe -> maybe.map(Result::<S, F>failure).orElseGet(() -> Result.success(onEmpty.get()));
    }

    /**
     * Takes a class and a mapping function and returns a function which takes a value and, if it's of the
     * provided class, applies the mapping function to it and returns it as a Success, otherwise returning
     * the input value as a Failure.
     */
    static <S, F, TF extends F> Function<F, Result<S, F>> ifType(Class<TF> targetClass, Function<TF, S> mapper) {
        return Introducers.<F, TF>castTo(targetClass).andThen(Transformers.onSuccess(mapper));
    }

    /**
     * Takes a predicate and a mapping function and returns a function which takes a value and, if it satisfies
     * the predicate, applies the mapping function to it and returns it as a Success, otherwise returning
     * the input value as a Failure.
     */
    static <S, F> Function<F, Result<S, F>> ifIs(Predicate<F> test, Function<F, S> mapper) {
        return input -> test.test(input)
            ? Result.success(mapper.apply(input))
            : Result.failure(input);
    }

    /**
     * Takes a predicate and a mapping function and returns a function which takes a value and, if it doesn't
     * satisfy the predicate, applies the mapping function to it and returns it as a Success, otherwise returning
     * the input value as a Failure.
     */
    static <S, F> Function<F, Result<S, F>> ifNot(Predicate<F> test, Function<F, S> mapper) {
        return ifIs(test.negate(), mapper);
    }

    /**
     * Takes a value and a mapping function and returns a function which takes a value and, if it is equal to
     * the provided value, applies the mapping function to it and returns it as a Success, otherwise returning
     * the input value as a Failure.
     */
    static <S, F> Function<F, Result<S, F>> ifEquals(F expectedValue, Function<F, S> mapper) {
        return input -> expectedValue.equals(input)
            ? Result.success(mapper.apply(input))
            : Result.failure(input);
    }

    /**
     * Matches the value if the provided function yields an Optional whose value is
     * present, returning the value in that Optional.
     */
    static <S, F> Function<F, Result<S, F>> ifPresent(Function<F, Optional<S>> successProvider) {
        return value -> successProvider
                .apply(value)
                .map(Result::<S, F>success)
                .orElseGet(() -> Result.failure(value));
    }
}
