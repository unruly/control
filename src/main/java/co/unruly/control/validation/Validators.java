package co.unruly.control.validation;


import co.unruly.control.Optionals;
import co.unruly.control.ThrowingLambdas.ThrowingFunction;
import co.unruly.control.result.Result;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static co.unruly.control.result.Result.failure;
import static co.unruly.control.result.Result.success;
import static co.unruly.control.result.Transformers.onFailure;
import static co.unruly.control.result.Transformers.recover;

public interface Validators {

    @SafeVarargs
    public static <T, E> Validator<T, E> compose(Validator<T, E>... validators) {
        return t -> Arrays.stream(validators).flatMap(v -> v.validate(t));
    }

    public static <T, E> Validator<T, E> rejectIf(Predicate<T> test, E error) {
        return acceptIf(test.negate(), error);
    }

    public static <T, E> Validator<T, E> rejectIf(Predicate<T> test, Function<T, E> errorGenerator) {
        return acceptIf(test.negate(), errorGenerator);
    }

    public static <T, E> Validator<T, E> acceptIf(Predicate<T> test, E error) {
        return acceptIf(test, t -> error);
    }

    public static <T, E> Validator<T, E> acceptIf(Predicate<T> test, Function<T, E> errorGenerator) {
        return t -> test.test(t) ? Stream.empty() : Stream.of(errorGenerator.apply(t));
    }

    public static <T, E> Validator<T, E> firstOf(Validator<T, E> validator) {
        return t -> Optionals.stream(validator.validate(t).findFirst());
    }

    public static <T, E> Validator<T, E> onlyIf(Predicate<T> test, Validator<T, E> validator) {
        return t -> test.test(t) ? validator.validate(t) : Stream.empty();
    }

    public static <T, E, E1> Validator<T, E1> mappingErrors(Validator<T, E> validator, BiFunction<T, E, E1> errorMapper) {
        return t -> validator.validate(t).map(e -> errorMapper.apply(t, e));
    }

    public static <T, T1, E> Validator<T, E> on(Function<T, T1> accessor, Validator<T1, E> innerValidator) {
        return t -> innerValidator.validate(accessor.apply(t));
    }

    public static <T, T1, E, X extends Exception> Validator<T, E> tryOn(ThrowingFunction<T, T1, X> accessor, Function<Exception, E> onException, Validator<T1, E> innerValidator) {
        return t -> {
            try {
                return innerValidator.validate(accessor.apply(t));
            } catch (Exception e) {
                return Stream.of(onException.apply(e));
            }
        };
    }

    public static <T, T1, E> Validator<T, E> onEach(Function<T, Iterable<T1>> iterator, Validator<T1, E> innerValidator) {
        return t -> StreamSupport.stream(iterator.apply(t).spliterator(), false).flatMap(innerValidator::validate);
    }

    public static <T, E> Validator<T, E> tryTo(Validator<T, E> validatorWhichThrowsRuntimeExceptions, Function<RuntimeException, E> errorMapper) {
        return t -> {
            try {
                return validatorWhichThrowsRuntimeExceptions.validate(t);
            } catch (RuntimeException ex) {
                return Stream.of(errorMapper.apply(ex));
            }
        };
    }

    public static <T, E> Function<Result<T, FailedValidation<T, E>>, Result<T, FailedValidation<T, E>>> ignoreWhen(Predicate<E> filterCondition) {
        return result -> result
                .then(onFailure(fv -> new FailedValidation<>(fv.value, fv.errors.stream().filter(filterCondition.negate()).collect(Collectors.toList()))))
                .then(recover(fv -> fv.errors.isEmpty() ? success(fv.value) : failure(fv)));
    }

}
