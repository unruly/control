package co.unruly.control.validation;

import co.unruly.control.linklist.LinkLists;
import co.unruly.control.result.Attempt;
import co.unruly.control.result.Results;

import java.util.List;

public class Validations {

    public static <T, E> Attempt<T, T, FailedValidation<T, E>, List<E>> treatFailuresAsList() {
        return Results.mapFailure(fv -> LinkLists.toList(fv.errors));
    }
}
