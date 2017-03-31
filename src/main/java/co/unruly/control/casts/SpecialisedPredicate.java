package co.unruly.control.casts;

import co.unruly.control.result.Result;

import java.util.function.Predicate;

/**
 * Created by tomj on 31/03/2017.
 */
public class SpecialisedPredicate<T, S extends T> implements Predicate<T> {

    private final Class<S> specialisation;
    private final Predicate<S> additionalTests;

    public static <S, T extends S> SpecialisedPredicate<S, T> instanceOf(Class<T> clazz) {
        return new SpecialisedPredicate<>(clazz);
    }

    private SpecialisedPredicate(Class<S> specialisation) {
        this(specialisation, __ -> true);
    }

    private SpecialisedPredicate(Class<S> specialisation, Predicate<S> additionalTests) {
        this.specialisation = specialisation;
        this.additionalTests = additionalTests;
    }

    @Override
    public boolean test(T t) {
        return castToSpecialisation(t).then(r -> r.either(__ -> true, __ -> false));
    }

    public SpecialisedPredicate<T, S> with(Predicate<S> secondaryTest) {
        return new SpecialisedPredicate<>(specialisation, additionalTests.and(secondaryTest));
    }

    private Result<S, T> castToSpecialisation(T t) {
        return Casts.cast(t, specialisation);
    }
}