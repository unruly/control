package co.unruly.control.Result;

public class Casts {

    @SuppressWarnings("unchecked")
    public static <T, S extends T> Result<S, T> cast(T t, Class<S> subclass) {
        return subclass.isAssignableFrom(t.getClass()) ? Result.success((S)t) : Result.failure(t);
    }

}