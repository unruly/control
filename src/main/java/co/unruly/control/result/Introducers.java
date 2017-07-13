package co.unruly.control.result;

import co.unruly.control.ThrowingLambdas;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.function.Function.identity;

/**
 * A collection of sample functions which take regular values and output a Result.
 */
public interface Introducers {

    /**
     * Returns a Function which creates a new Success wrapping the provided value
     */
    static <S, F> Function<S, Result<S, F>> success() {
        return Result::success;
    }

    /**
     * Returns a Function which creates a new Failure wrapping the provided value
     */
    static <S, F> Function<F, Result<S, F>> failure() {
        return Result::failure;
    }

    /**
     * Returns a function which takes an Optional value, and returns a success of the
     * wrapped value if it was present, otherwise returns a failure using the provided Supplier
     */
    static <S, F> Function<Optional<S>, Result<S, F>> fromOptional(Supplier<F> onEmpty) {
        return maybe -> maybe.map(Result::<S, F>success).orElseGet(() -> Result.failure(onEmpty.get()));
    }

    /**
     * Returns a function which takes an Optional value, and returns a failure of the
     * wrapped value if it was present, otherwise returns a success using the provided Supplier
     */
    static <S, F> Function<Optional<F>, Result<S, F>> fromOptionalFailure(Supplier<S> onEmpty) {
        return maybe -> maybe.map(Result::<S, F>failure).orElseGet(() -> Result.success(onEmpty.get()));
    }

    /**
     * Returns a function which takes a value, applies the provided function to it, and returns
     * a success of the output of that function, or a failure of the exception thrown by that function
     * if it threw an exception.
     *
     * Whilst we take a ThrowingFunction which throws a specific checked exception type X, our
     * eventual Result is of the more general type Exception. That's because it's also possible for the
     * function to throw other types of RuntimeException, and we have two choices: don't catch (or rethrow)
     * RuntimeException, or have a more general failure type. Rethrowing exceptions goes against the whole
     * point of constraining the error path, so we opt for the latter.
     *
     * If the provided function throws an Error, we don't catch that. Errors in general are not
     * intended to be caught.
     *
     * Note that idiomatic handling of Exceptions as failure type does allow specialised catch blocks
     * on specific exception types.
     */
    static <IS, OS, X extends Exception> Function<IS, Result<OS, Exception>> tryTo(
        ThrowingLambdas.ThrowingFunction<IS, OS, X> throwingFunction
    ) {
        return tryTo(throwingFunction, identity());
    }

    /**
     * Returns a function which takes a value, applies the provided function to it, and returns
     * a success of the output of that function. In the case where the function throws an exception,
     * that exception is passed to the provided exception-mapper, and the output of that call is the
     * failure value.
     *
     * Whilst we take a ThrowingFunction which throws a specific checked exception type X, our
     * eventual Result is of the more general type Exception. That's because it's also possible for the
     * function to throw other types of RuntimeException, and we have two choices: don't catch (or rethrow)
     * RuntimeException, or have a more general failure type. Rethrowing exceptions goes against the whole
     * point of constraining the error path, so we opt for the latter.
     *
     * If the provided function throws an Error, we don't catch that. Errors in general are not
     * intended to be caught.
     *
     * Note that idiomatic handling of Exceptions as failure type does allow specialised catch blocks
     * on specific exception types.
     */
    static <IS, OS, X extends Exception, F> Function<IS, Result<OS, F>> tryTo(
        ThrowingLambdas.ThrowingFunction<IS, OS, X> throwingFunction,
        Function<Exception, F> exceptionMapper
    ) {
        return input -> {
            try {
                return Result.success(throwingFunction.apply(input));
            } catch (Exception ex) {
                return Result.failure(exceptionMapper.apply(ex));
            }
        };
    }

    /**
     * Takes a class and returns a function which takes a value, attempts to cast it to that class, and returns
     * a Success of the provided type if it's a member of it, and a Failure of the known type otherwise, in both
     * cases containing the input value.
     *
     * This differs from exactCastTo in that exactCastTo will only return a Success if the given value is exactly
     * the target type, whereas this will also return a Success if it is a subtype of that type.
     */
    static <IS, OS extends IS> Function<IS, Result<OS, IS>> castTo(Class<OS> targetClass) {
        return input -> targetClass.isAssignableFrom(input.getClass())
            ? Result.success((OS)input)
            : Result.failure(input);
    }

    /**
     * Takes a class and returns a function which takes a value, attempts to cast it to that class, and returns
     * a Success of the provided type if it's the same type as it, and a Failure of the known type otherwise, in both
     * cases containing the input value.
     *
     * This differs from castTo in that castTo will return a Success if the given value is a subtype of the target
     * type, whereas this will only return a Success if it is exactly that type.
     */
    static <IS, OS extends IS> Function<IS, Result<OS, IS>> exactCastTo(Class<OS> targetClass) {
        return input -> targetClass.equals(input.getClass())
            ? Result.success((OS)input)
            : Result.failure(input);
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
