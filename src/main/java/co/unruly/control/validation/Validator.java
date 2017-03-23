package co.unruly.control.validation;

import co.unruly.control.linklist.LinkList;
import co.unruly.control.linklist.LinkLists;
import co.unruly.control.result.Result;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static co.unruly.control.linklist.NonEmptyList.cons;
import static java.util.stream.Collectors.toList;

@FunctionalInterface
public interface Validator<T, E> extends Function<T, Result<T, FailedValidation<T, E>>> {

    default Result<T, FailedValidation<T, E>> apply(T item) {
        LinkList<E> errors = LinkLists.of(validate(item).collect(toList()));
        return errors.read(
                (x, xs) -> Result.failure(new FailedValidation<>(item, cons(x, xs))),
                () -> Result.success(item)
        );
    }

    Stream<E> validate(T item);

}
