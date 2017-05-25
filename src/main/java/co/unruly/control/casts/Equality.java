package co.unruly.control.casts;

import co.unruly.control.result.Resolvers;

import java.util.function.BiPredicate;

import static co.unruly.control.casts.Casts.cast;
import static co.unruly.control.result.Resolvers.ifFailed;
import static co.unruly.control.result.Transformers.onSuccess;

public interface Equality {

    static <T> boolean areEqual(T self, Object other, Class<T> clazz, BiPredicate<T, T> equalityChecker) {
        if(self==other) {
            return true;
        }

        return cast(other, clazz)
            .then(onSuccess(o -> equalityChecker.test(self, o)))
            .then(ifFailed(__ -> false));
    }
}
