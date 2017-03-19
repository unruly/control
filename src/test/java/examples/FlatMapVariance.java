package examples;

import co.unruly.control.result.Result;
import co.unruly.control.result.Types;
import co.unruly.control.validation.Validator;
import co.unruly.control.validation.Validators;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static co.unruly.control.result.Results.*;
import static co.unruly.control.validation.Validators.rejectIf;

public class FlatMapVariance {

    private static Validator<Integer, String> fizzbuzz = Validators.compose(
        rejectIf(n -> n % 3 == 0, "fizz"),
        rejectIf(n -> n % 5 == 0, "buzz"));

    private static Validator<String, String> under100 = Validators.compose(
        rejectIf(s -> s.length() > 2, s -> s + " is too damn high")
    );

    public void canFlatmapErrorTypeOfStringIntoErrorTypeOfString() {
        divideExactlyByTwo(3)
            .then(flatMap(this::isPrime));
    }

//    // this should not compile
//    public void cannotFlatmapErrorTypeOfListOfStringIntoErrorTypeOfString() {
//        Result<Integer, Object> foo = divideExactlyByTwo(4)
//            .then(flatMap(this::listFactors));
//    }

    public String isThisAnInterview(int m) {
        Validator<Integer, String> fizzbuzz = Validators.compose(
            rejectIf(n -> n % 3 == 0, "fizz"),
            rejectIf(n -> n % 5 == 0, "buzz"));

        Validator<String, String> under100 = rejectIf(s -> s.length() > 2, s -> s + " is too damn high");

        return with(m,
            (fizzbuzz
                .then(map(x -> Integer.toString(x)))
                .then(Types.<List<String>>forFailures().convert())
                .then(flatMap(under100)))
                .then(map(s -> "Great success! " + s))
                .then(mapFailure(f -> "Big fails :( " + String.join(", ", f)))
                .andFinally(collapse()));
    }

    public void canFlatmapErrorTypeOfFailedValidationIntoErrorTypeOfListOfString() {
        Result<Integer, List<String>> foo = fizzbuzz.apply(4)
            .then(Types.<List<String>>forFailures().convert())
            .then(flatMap(this::listFactors));
    }

    public void canFlatmapErrorTypeOfListOfStringIntoErrorTypeOfFailedValidation() {
        Result<Integer, List<String>> foo = listFactors(5)
            .then(flatMap(fizzbuzz));
    }

    private Result<Integer, String> divideExactlyByTwo(int number) {
        return number % 2 == 0
            ? Result.success(number / 2)
            : Result.failure(number + " is odd: cannot divide exactly by two");
    }

    private Result<Integer, String> isPrime(int number) {
        return IntStream.range(2, (int) Math.sqrt(number))
            .anyMatch(possibleDivisor -> number % possibleDivisor == 0)
                ? Result.success(number)
                : Result.failure(number + " is not prime");
    }

    private boolean checkPrime(int number) {
        return IntStream
                .range(2, (int) Math.sqrt(number))
                .anyMatch(possibleDivisor -> number % possibleDivisor == 0);
    }

    private Result<Integer, List<String>> listFactors(int number) {
        List<String> primeFactors = IntStream
            .range(2, (int) Math.sqrt(number))
            .filter(possibleDivisor -> number % possibleDivisor == 0)
            .mapToObj(divisor -> number + " is divisible by " + divisor)
            .collect(Collectors.toList());

        return primeFactors.isEmpty()
            ? Result.success(number)
            : Result.failure(primeFactors);
    }
}
