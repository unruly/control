package co.unruly.control.Validation;

import co.unruly.control.LinkList.LinkList;
import co.unruly.control.LinkList.LinkLists;
import co.unruly.control.Result.Result;

import java.util.function.Function;
import java.util.stream.Stream;

import static co.unruly.control.LinkList.NonEmptyList.cons;
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
