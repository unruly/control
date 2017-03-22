package co.unruly.control.result;

import java.util.stream.Stream;

/**
 * Convenience functional interface for when an Attempt has the same input and output types,
 * as the type signatures can get very noisy otherwise.
 *
 * Note that we can compose EndoAttempts variadically, where we can't with regular Attempts, as
 * they're all like-typed. This is useful in some situations like Matches or Validations, where we
 * want to construct concise DSLs.
 */
@FunctionalInterface
public interface EndoAttempt<S, F> extends Attempt<S, S, F, F> {

    /**
     * Compose two EndoAttempts together, executing first this EndoAttempt and then the provided EndoAttempt.
     */
    default EndoAttempt<S, F> then(EndoAttempt<S, F> f) {
        return f.compose(this)::apply;
    }
}
