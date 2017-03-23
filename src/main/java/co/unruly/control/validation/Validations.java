package co.unruly.control.validation;

import co.unruly.control.linklist.LinkLists;
import co.unruly.control.result.Result;
import co.unruly.control.result.Results;

import java.util.List;
import java.util.function.Function;

public class Validations {

    public static <T, E> Function<Result<T, FailedValidation<T, E>>, Result<T, List<E>>> treatFailuresAsList() {
        return Results.mapFailure(fv -> LinkLists.toList(fv.errors));
    }
}
