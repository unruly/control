package co.unruly.control.Validation;

import co.unruly.control.LinkList.LinkLists;
import co.unruly.control.Result.Result;
import co.unruly.control.Result.Results;

import java.util.List;
import java.util.function.Function;

public class Validations {

    public static <T, E> Function<Result<T, FailedValidation<T, E>>, Result<T, List<E>>> treatFailuresAsList() {
        return v -> v.then(Results.mapFailures(fv -> LinkLists.toList(fv.errors)));
    }
}
