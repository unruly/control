package co.unruly.control.validation;

import co.unruly.control.linklist.LinkList;
import co.unruly.control.linklist.LinkLists;
import co.unruly.control.result.EndoAttempt;
import co.unruly.control.result.Result;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static co.unruly.control.linklist.NonEmptyList.cons;
import static java.util.stream.Collectors.toList;

@FunctionalInterface
public interface Validator<T, E> extends EndoAttempt<T, FailedValidation<T, E>> {

    default Result<T, FailedValidation<T, E>> lifting(T item) {
        LinkList<E> errors = LinkLists.of(validate(item).collect(toList()));
        return errors.read(
                (x, xs) -> Result.failure(new FailedValidation<>(item, cons(x, xs))),
                () -> Result.success(item)
        );
    }

    default Result<T, FailedValidation<T, E>> apply(Result<T, FailedValidation<T, E>> r) {
        return r.either(
            this::lifting,
            failure -> Result.failure(new FailedValidation<>(
                failure.value,
                cons(failure.errors.first, LinkLists.eagerConcat(
                    failure.errors.rest,
                    LinkLists.of(validate(failure.value).collect(Collectors.toList()))))))
        );
    }

    Stream<E> validate(T item);

}
