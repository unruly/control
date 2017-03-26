package co.unruly.control.validation;

import co.unruly.control.result.Result;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@FunctionalInterface
public interface Validator<T, E> extends Function<T, Result<T, FailedValidation<T, E>>> {

    default Result<T, FailedValidation<T, E>> apply(T item) {
        List<E> errors = validate(item).collect(toList());
        return errors.isEmpty()
            ? Result.success(item)
            : Result.failure(new FailedValidation<T, E>(item, errors));
    }

    Stream<E> validate(T item);

}
