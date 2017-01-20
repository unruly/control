package co.unruly.control.Validation;

import co.unruly.control.LinkList.LinkLists;
import co.unruly.control.LinkList.NonEmptyList;

import java.util.Objects;

public final class FailedValidation<T, E> {

    public final T value;
    public final NonEmptyList<E> errors;

    public FailedValidation(T value, NonEmptyList<E> errors) {
        this.value = value;
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "FailedValidation{" +
                "value=" + value +
                ", errors=" + errors +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailedValidation<?, ?> that = (FailedValidation<?, ?>) o;
        return Objects.equals(value, that.value) &&
               LinkLists.listsEqual(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, errors);
    }
}
