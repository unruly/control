package co.unruly.control.validation;

import java.util.List;
import java.util.Objects;

public final class FailedValidation<T, E> implements ForwardingList<E> {

    public final T value;
    public final List<E> errors;

    public FailedValidation(T value, List<E> errors) {
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
               Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, errors);
    }

    @Override
    public List<E> delegate() {
        return errors;
    }
}
