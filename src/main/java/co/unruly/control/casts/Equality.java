package co.unruly.control.casts;

import java.util.function.BiPredicate;

import static co.unruly.control.HigherOrderFunctions.with;
import static co.unruly.control.result.Introducers.exactCastTo;
import static co.unruly.control.result.Resolvers.ifFailed;
import static co.unruly.control.result.Transformers.onSuccess;

public interface Equality {

    static <T> boolean areEqual(T self, Object other, BiPredicate<T, T> equalityChecker) {
        if(self==other) {
            return true;
        }

        if(other==null) {
            return false;
        }

        return with(other, exactCastTo((Class<T>)self.getClass()))
            .then(onSuccess(o -> equalityChecker.test(self, o)))
            .then(ifFailed(__ -> false));
    }
}
