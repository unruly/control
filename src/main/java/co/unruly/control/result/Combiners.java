package co.unruly.control.result;

import java.util.function.BiFunction;
import java.util.function.Function;

import static co.unruly.control.result.Result.success;

public interface Combiners {

    /**
     * Combines two Results into a single Result. If both arguments are a Success, then
     * it applies the given function to their values and returns a Success of it.
     *
     * If either or both arguments are Failures, then this returns the first failure
     * it encountered.
     */
    public static <A, B, F> Function<Result<A, F>, MergeableResults<A, B, F>> combineWith(Result<B, F> secondArgument) {
        // ugh ugh ugh we need an abstract class because otherwise it can't infer generics properly can i be sick now? ta
        return result -> new MergeableResults<A, B, F>() {
            @Override
            public <C> Result<C, F> using(BiFunction<A, B, C> combiner) {
                return result.either(
                    s1 -> secondArgument.either(
                        s2 -> success(combiner.apply(s1, s2)),
                        Result::failure
                    ),
                    Result::failure
                );
            }
        };
    }

    @FunctionalInterface
    public interface MergeableResults<A, B, F>  {
        <C> Result<C, F> using(BiFunction<A, B, C> combiner);
    }
}
