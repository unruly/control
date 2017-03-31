package co.unruly.control.casts;

import co.unruly.control.result.Result;

import java.util.function.Function;

/**
 * Created by tomj on 31/03/2017.
 */
public interface Casts {

    static <S, T extends S> Result<T, S> cast(S item, Class<T> specialisedClass) {
        return specialisedClass.isAssignableFrom(item.getClass())
                ? Result.success((T)item)
                : Result.failure(item);
    }

    /**
     * Takes a class and returns a function which takes a value, attempts to cast it to that class, and returns
     * a Success of the provided type if it's a member of it, and a Failure of the known type otherwise, in both
     * cases containing the input value.
     */
    static <IS, OS extends IS> Function<IS, Result<OS, IS>> castTo(Class<OS> targetClass) {
        return input -> cast(input, targetClass);
    }
}
