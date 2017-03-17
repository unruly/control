package examples;

import co.unruly.control.result.Match;
import co.unruly.control.result.Result;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static co.unruly.control.Unit.UNIT;
import static co.unruly.control.result.Try.tryTo;
import static java.util.stream.Collectors.toList;

public class ExceptionsInStreamsHandling {


    @Test
    public void handling_exceptions_with_result_example() {

        List<Integer> customerAges = Stream.of("Bob", "Bill")
            .map(tryTo(this::findCustomerByName))
            .peek(onSuccess(this::sendEmailUpdateTo))
            .map(onSuccess(Customer::age))
            .map(recover(NoCustomerWithThatName.class, error -> {
                log("Customer not found :(");
                return -1;
            }))
            .map(recover(IOException.class, error -> -2))
            .map(orElse(__ -> -127))
            .collect(toList());

    }

    public static <IS, OS, F> Function<Result<IS, F>, Result<OS,F>> onSuccess(Function<IS, OS> function) {
        return originalResult -> originalResult.either(function.andThen(Result::success), Result::failure);
    }

    public static <S, F> Consumer<Result<S, F>> onSuccess(Consumer<S> consumer) {
        return result -> result.either(success -> { consumer.accept(success); return UNIT; } , f -> UNIT);
    }

    public static <S, F, EF extends F> Function<Result<S, F>, Result<S, F>> recover(Class<EF> clazz, Function<EF, S> recoveryFunction) {
        return Match.<S, F, EF>ifType(clazz, recoveryFunction)::onResult;
    }

    public static <FS, IS extends FS, F, OS extends FS> Function<Result<IS, F>, FS> orElse(Function<F, OS> recovery) {
        return result -> result.either(i -> i, recovery);
    }

    static class ConsumerCalled extends RuntimeException {}
    static class CheckedConsumerCalled extends Exception {}

    public String operationThatThrows(String input) throws ACheckedException {
        if (Objects.equals("DoNotThrow", input)) {
            return "SuccessfulResult";
        } else if (Objects.equals("ThrowSubtype", input)) {
            throw new SubtypeOfACheckedException();
        }
        throw new ACheckedException();
    }

    public static class ACheckedException extends Exception {}
    public static class NotThrownException extends ACheckedException {}
    public static class SubtypeOfACheckedException extends ACheckedException {}

    public static class CheckedAssertionException extends Exception {
        public CheckedAssertionException(String message) {
            super(message);
        }
    }

    static class UnexpectedException extends RuntimeException {}
    static class CustomerNotFound extends Exception {}
    static class CustomerUnsubscribed extends CustomerNotFound {}
    static class NoCustomerWithThatName extends CustomerNotFound {}

    public Customer findCustomerByName(String name) throws CustomerNotFound {
        return customer;
    }

    private void sendEmailUpdateTo(Customer potentialCustomer) {
        email(customer.emailAddress(), customer.name(),  "Blah blah blah");
    }

    private void email(String email, String name, String message) {

    }

    private void log(String s) {

    }

    public interface Customer {
        default void sendEmail(String s) {}
        default void updateLastSpammedDate(){};

        default String emailAddress(){ return "";}
        default String name() { return ""; }
        default int age() { return 0; }

        default int calculateValue() throws CostUnknown { return 0; }
        class CostUnknown extends Exception {}
    }

    static Customer customer = new Customer(){};


}
