package co.unruly.control.validation;

import co.unruly.control.linklist.LinkList;
import co.unruly.control.linklist.LinkLists;
import co.unruly.control.linklist.NonEmptyList;
import co.unruly.control.Pair;
import co.unruly.control.result.Result;
import co.unruly.control.result.Results;
import co.unruly.control.ThrowingLambdas;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static co.unruly.control.linklist.LinkLists.lazyMap;
import static co.unruly.control.linklist.LinkLists.nonEmptyList;
import static co.unruly.control.linklist.NonEmptyList.cons;
import static co.unruly.control.result.Results.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class ValidatorTest {

    @Test
    public void canCreateValidatorsWithFixedErrorMessages() {
        Validator<Integer, String> isEven = Validators.acceptIf(divisibleBy(2), "odd");

        Result<Integer, FailedValidation<Integer, String>> validate4 = isEven.apply(4);
        Result<Integer, FailedValidation<Integer, String>> validate5 = isEven.apply(5);

        assertThat(validate4, is(success(4)));

        assertThat(validate5, is(failure(5, "odd")));
    }


    @Test
    public void canCreateValidatorsWithDynamicErrorMessages() {
        Validator<Integer, String> isEven = Validators.acceptIf(divisibleBy(2), x -> String.format("%d is odd", x));

        Result<Integer, FailedValidation<Integer, String>> validate4 = isEven.apply(4);
        Result<Integer, FailedValidation<Integer, String>> validate5 = isEven.apply(5);

        assertThat(validate4, is(success(4)));

        assertThat(validate5, is(failure(5, "5 is odd")));
    }

    @Test
    public void canComposeValidators() {
        Validator<Integer, String> fizzbuzz = Validators.compose(
                Validators.rejectIf(divisibleBy(3), "fizz"),
                Validators.rejectIf(divisibleBy(5), x -> String.format("%d is a buzz", x)));

        Result<Integer, FailedValidation<Integer, String>> validate4 = fizzbuzz.apply(4);
        Result<Integer, FailedValidation<Integer, String>> validate5 = fizzbuzz.apply(15);

        assertThat(validate4, is(success(4)));

        assertThat(validate5, is(failure(15, "fizz", "15 is a buzz")));
    }

    @Test
    public void canComposeValidatorsForFirstError() {
        Validator<Integer, String> fizzbuzz = Validators.firstOf(Validators.compose(
                Validators.rejectIf(divisibleBy(3), "fizz"),
                Validators.rejectIf(divisibleBy(5), x -> String.format("%d is a buzz", x))));

        Result<Integer, FailedValidation<Integer, String>> validate5 = fizzbuzz.apply(5);
        Result<Integer, FailedValidation<Integer, String>> validate15 = fizzbuzz.apply(15);

        assertThat(validate5, is(failure(5, "5 is a buzz")));
        assertThat(validate15, is(failure(15, "fizz")));
    }

    @Test
    public void doesNotExecuteValidatorsIfAlreadyFailedAndOnlyReportingFirst() {
        Validator<Integer, String> fizzbuzz = Validators.firstOf(Validators.compose(
                Validators.rejectIf(divisibleBy(3), "fizz"),
                Validators.rejectIf(divisibleBy(5), x -> { throw new AssertionError("should not exercise this method"); })));

        Validator<Integer, String> biglittle = Validators.firstOf(Validators.compose(
                Validators.rejectIf(x -> x > 10, "big"),
                Validators.rejectIf(x -> x < 3, x -> { throw new AssertionError("should not exercise this method"); })));

        Validator<Integer, String> combined = Validators.compose(fizzbuzz, biglittle);

        Result<Integer, FailedValidation<Integer, String>> validate15 = combined.apply(15);

        assertThat(validate15, is(failure(15, "fizz", "big")));
    }

    @Test
    public void canCreateValidatorsFromFunctionsThatReturnOptionals() {
        Validator<List<Integer>, String> containsEvens = Validators.validate(xs -> {
            final List<String> evenNumbers = xs.stream().filter(x -> x % 2 == 0).map(x -> Integer.toString(x)).collect(toList());
            return evenNumbers.isEmpty()
                    ? Optional.empty()
                    : Optional.of(String.format("Even numbers [%s] found", String.join(", ", evenNumbers)));
        });

        Result<List<Integer>, FailedValidation<List<Integer>, String>> someOdds = containsEvens.apply(asList(1, 3, 7));
        Result<List<Integer>, FailedValidation<List<Integer>, String>> mixedNums = containsEvens.apply(asList(1, 2, 3, 42, 99));

        assertThat(someOdds, is(success(asList(1, 3, 7))));

        assertThat(mixedNums, is(failure(asList(1, 2, 3, 42, 99), "Even numbers [2, 42] found")));
    }

    @Test
    public void canCreateValidatorsFromFunctionsThatReturnOptionalsWithAFormatter() {
        Validator<List<Integer>, String> containsEvens = Validators.validate(
                xs -> nonEmptyList(xs.stream().filter(x -> x % 2 == 0).collect(toList())),
                evens -> String.format("Even numbers [%s] found", String.join(", ", lazyMap(evens, Object::toString)))
        );

        Result<List<Integer>, FailedValidation<List<Integer>, String>> someOdds = containsEvens.apply(asList(1, 3, 7));
        Result<List<Integer>, FailedValidation<List<Integer>, String>> mixedNums = containsEvens.apply(asList(1, 2, 3, 42, 99));

        assertThat(someOdds, is(success(asList(1, 3, 7))));

        assertThat(mixedNums, is(failure(asList(1, 2, 3, 42, 99), "Even numbers [2, 42] found")));
    }

    @Test
    public void canStreamSuccesses() {
        Validator<Integer, String> isEven = Validators.acceptIf(divisibleBy(2), "odd");

        List<Integer> evens = Stream.of(1,2,3,4,5,6,7,8,9)
                .map((item) -> isEven.apply(item))
                .flatMap(Results.successes())
                .collect(toList());

        assertThat(evens, hasItems(2,4,6,8));
    }

    @Test
    public void canStreamFailures() {
        Validator<Integer, String> isEven = Validators.acceptIf(divisibleBy(2), "odd");

        List<FailedValidation<Integer, String>> odds = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .map((item) -> isEven.apply(item))
                .flatMap(Results.failures())
                .collect(toList());

        assertThat(odds, hasItems(
                validationFailure(1, "odd"),
                validationFailure(3, "odd"),
                validationFailure(5, "odd"),
                validationFailure(7, "odd"),
                validationFailure(9, "odd")));
    }

    @Test
    public void canConsumeSuccesses() {
        Consumer<Integer> log = mock(Consumer.class);

        Validator<Integer, String> isPrime = Validators.compose(
                Validators.rejectIf(multipleOf(2), x -> x + " divides by 2"),
                Validators.rejectIf(multipleOf(3), x -> x + " divides by 3"),
                Validators.rejectIf(multipleOf(5), x -> x + " divides by 5"),
                Validators.rejectIf(multipleOf(7), x -> x + " divides by 7")
        );

        Stream.of(1,2,3,4,5,6,7,8,9).map((item) -> isPrime.apply(item)).forEach(onSuccess(log));

        verify(log).accept(1);
        verify(log).accept(2);
        verify(log).accept(3);
        verify(log).accept(5);
        verify(log).accept(7);
        verifyNoMoreInteractions(log);
    }

    @Test
    public void canConsumeFailures() {
        Consumer<FailedValidation<Integer, String>> log = mock(Consumer.class);

        Validator<Integer, String> isPrime = Validators.compose(
                Validators.rejectIf(multipleOf(2), x -> x + " divides by 2"),
                Validators.rejectIf(multipleOf(3), x -> x + " divides by 3"),
                Validators.rejectIf(multipleOf(5), x -> x + " divides by 5"),
                Validators.rejectIf(multipleOf(7), x -> x + " divides by 7")
        );

        Stream.of(1,2,3,4,5,6,7,8,9).map((item) -> isPrime.apply(item)).forEach(onFailure(log));

        verify(log).accept(validationFailure(4, "4 divides by 2"));
        verify(log).accept(validationFailure(6, "6 divides by 2", "6 divides by 3"));
        verify(log).accept(validationFailure(8, "8 divides by 2"));
        verify(log).accept(validationFailure(9, "9 divides by 3"));
        verifyNoMoreInteractions(log);
    }

    @Test
    public void canFireFirstErrorForEachFailure() {
        Consumer<FailedValidation<Integer, String>> log = mock(Consumer.class);

        Validator<Integer, String> isPrime = Validators.firstOf(Validators.compose(
                Validators.rejectIf(multipleOf(2), x -> x + " divides by 2"),
                Validators.rejectIf(multipleOf(3), x -> x + " divides by 3"),
                Validators.rejectIf(multipleOf(5), x -> x + " divides by 5"),
                Validators.rejectIf(multipleOf(7), x -> x + " divides by 7")
        ));

        Stream.of(1,2,3,4,5,6,7,8,9).map((item) -> isPrime.apply(item)).forEach(onFailure(log));

        verify(log).accept(validationFailure(4, "4 divides by 2"));
        verify(log).accept(validationFailure(6, "6 divides by 2"));
        verify(log).accept(validationFailure(8, "8 divides by 2"));
        verify(log).accept(validationFailure(9, "9 divides by 3"));
        verifyNoMoreInteractions(log);
    }

    @Test
    public void canCreateConditionalValidator() {
        Validator<List<Integer>, String> containsEvens = Validators.acceptIf(
                list -> list.stream().filter(x -> x % 2 == 0).collect(Collectors.toList()).isEmpty(),
                "List contains even numbers");

        Validator<List<Integer>, String> onlyChecksEvenLengthLists = Validators.onlyIf(
                list -> list.size() % 2 == 0,
                containsEvens
        );

        Result<List<Integer>, FailedValidation<List<Integer>, String>> ofFiveNumbers = onlyChecksEvenLengthLists.apply(asList(1, 2, 3, 4, 5));
        Result<List<Integer>, FailedValidation<List<Integer>, String>> ofSixNumbers = onlyChecksEvenLengthLists.apply(asList(1, 2, 3, 4, 5, 6));

        assertThat(ofFiveNumbers, is(success(asList(1,2,3,4,5))));
        assertThat(ofSixNumbers, is(failure(asList(1,2,3,4,5,6), "List contains even numbers")));
    }

    @Test
    public void canFireAllErrorsForEachFailure() {
        BiConsumer<Integer, String> log = mock(BiConsumer.class);

        Validator<Integer, String> isPrime = Validators.compose(
                Validators.rejectIf(multipleOf(2), x -> x + " divides by 2"),
                Validators.rejectIf(multipleOf(3), x -> x + " divides by 3"),
                Validators.rejectIf(multipleOf(5), x -> x + " divides by 5"),
                Validators.rejectIf(multipleOf(7), x -> x + " divides by 7")
        );

        Stream.of(1,2,3,4,5,6,7,8,9).map((item) -> isPrime.apply(item)).forEach(onFailure(v -> v.errors.forEach(e -> log.accept(v.value, e))));

        verify(log).accept(4, "4 divides by 2");
        verify(log).accept(6, "6 divides by 2");
        verify(log).accept(6, "6 divides by 3");
        verify(log).accept(8, "8 divides by 2");
        verify(log).accept(9, "9 divides by 3");
        verifyNoMoreInteractions(log);
    }

    @Test
    public void canMapErrors() {
        BiConsumer<Integer, String> log = mock(BiConsumer.class);

        Validator<Integer, String> isPrime = Validators.mappingErrors(Validators.compose(
                Validators.rejectIf(multipleOf(2), x -> x + " divides by 2"),
                Validators.rejectIf(multipleOf(3), x -> x + " divides by 3"),
                Validators.rejectIf(multipleOf(5), x -> x + " divides by 5"),
                Validators.rejectIf(multipleOf(7), x -> x + " divides by 7")
        ), (num, msg) -> msg + ", oh boy");

        Stream.of(1,2,3,4,5,6,7,8,9).map((item) -> isPrime.apply(item)).forEach(onFailure(v -> v.errors.forEach(e -> log.accept(v.value, e))));

        verify(log).accept(4, "4 divides by 2, oh boy");
        verify(log).accept(6, "6 divides by 2, oh boy");
        verify(log).accept(6, "6 divides by 3, oh boy");
        verify(log).accept(8, "8 divides by 2, oh boy");
        verify(log).accept(9, "9 divides by 3, oh boy");
        verifyNoMoreInteractions(log);
    }

    @Test
    public void canSplitResults() {
        Validator<Integer, String> isPrime = Validators.compose(
                Validators.rejectIf(multipleOf(2), x -> x + " divides by 2"),
                Validators.rejectIf(multipleOf(3), x -> x + " divides by 3"),
                Validators.rejectIf(multipleOf(5), x -> x + " divides by 5"),
                Validators.rejectIf(multipleOf(7), x -> x + " divides by 7")
        );

        Pair<List<Integer>, List<FailedValidation<Integer, String>>> results = Stream
                .of(4,5,6,7,8)
                .map((item) -> isPrime.apply(item))
                .collect(split());

        assertThat(results.left, hasItems(5, 7));
        assertThat(results.right, hasItems(
                validationFailure(4, "4 divides by 2"),
                validationFailure(6, "6 divides by 2", "6 divides by 3"),
                validationFailure(8, "8 divides by 2")
                ));
    }

    @Test
    public void blammo() {
        safelyDoSomethingDodgy(x -> { throw new Exception("hello"); }, "cheese");
    }

    private static void safelyDoSomethingDodgy(ThrowingLambdas.ThrowingConsumer<String, Exception> consumer, String message) {
        try {
            consumer.accept(message);
        } catch (Exception ex) {
            // do nothing cos that's how I roll

        }
    }

    private static void doSomethingDodgy(String message) throws Exception {
        throw new Exception(message);
    }

    private static Predicate<Integer> divisibleBy(int factor) {
        return x -> x % factor == 0;
    }

    private static Predicate<Integer> multipleOf(int factor) {
        return x -> x != factor && x % factor == 0;
    }

    private <T, E> Result<T, FailedValidation<T, E>> success(T i) {
        return Result.success(i);
    }

    @SafeVarargs
    private final <T, E> Result<T, FailedValidation<T, E>> failure(T value, E firstError, E... laterErrors) {
        return Result.failure(validationFailure(value, firstError, laterErrors));
    }

    @SafeVarargs
    private final <T, E> FailedValidation<T, E> validationFailure(T value, E firstError, E... laterErrors) {
        LinkList<E> restOfTheErrors = LinkLists.of(laterErrors);
        NonEmptyList<E> atLeastOneError = cons(firstError, restOfTheErrors);
        return new FailedValidation<>(value, atLeastOneError);
    }
}
