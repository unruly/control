package co.unruly.control.Validation;

import co.unruly.control.LinkList.LinkList;
import co.unruly.control.LinkList.LinkLists;
import co.unruly.control.Result.EndoAttempt;
import co.unruly.control.Result.Result;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static co.unruly.control.LinkList.NonEmptyList.cons;
import static java.util.stream.Collectors.toList;

@FunctionalInterface
public interface Validator<T, E> extends EndoAttempt<T, FailedValidation<T, E>> {

    default Result<T, FailedValidation<T, E>> apply(T item) {
        LinkList<E> errors = LinkLists.of(validate(item).collect(toList()));
        return errors.read(
                (x, xs) -> Result.failure(new FailedValidation<>(item, cons(x, xs))),
                () -> Result.success(item)
        );
    }

    default Result<T, FailedValidation<T, E>> onResult(Result<T, FailedValidation<T, E>> r) {
        return r.either(
            this::apply,
            failure -> Result.failure(new FailedValidation<>(
                failure.value,
                cons(failure.errors.first, LinkLists.eagerConcat(
                    failure.errors.rest,
                    LinkLists.of(validate(failure.value).collect(Collectors.toList()))))))
        );
    }

    Stream<E> validate(T item);

}
