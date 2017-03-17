package examples;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static co.unruly.control.result.StreamingResults.*;
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
            .map(failuresTo(__ -> -127))
            .collect(toList());

    }

    @Test
    public void handling_multiple_exceptions_with_result_example() {

        List<Integer> customerValues = Stream.of("Bob", "Bill")
            .map(tryTo(this::findCustomerByName))
            .peek(onSuccess(this::sendEmailUpdateTo))
            .map(onSuccessTry(Customer::calculateValue))
            .map(onSuccessTry(x -> x * 2))
            .map(recover(NoCustomerWithThatName.class, error -> {
                log("Customer not found :(");
                return -1;
            }))
            .map(recover(IOException.class, error -> -2))
            .map(failuresTo(() -> -127))
            .collect(toList());

    }


    static class CustomerNotFound extends Exception {}
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
        default String emailAddress(){ return "";}
        default String name() { return ""; }
        default int age() { return 0; }

        default int calculateValue() throws CostUnknown { return 0; }
        class CostUnknown extends Exception {}
    }

    static Customer customer = new Customer(){};


}
