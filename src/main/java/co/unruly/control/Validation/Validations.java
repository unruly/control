package co.unruly.control.Validation;

import co.unruly.control.LinkList.LinkLists;
import co.unruly.control.Result.Attempt;
import co.unruly.control.Result.Results;

import java.util.List;

public class Validations {

    public static <T, E> Attempt<T, T, FailedValidation<T, E>, List<E>> treatFailuresAsList() {
        return Results.mapFailure(fv -> LinkLists.toList(fv.errors));
    }
}
