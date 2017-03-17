[![Build Status](https://travis-ci.org/unruly/control.svg?branch=master)](https://travis-ci.org/unruly/control)

## Control

co.unruly.Control is a collection of functional control-flow primitives and utilities, built around a `Result` type.

### Result

A `Result<S, F>` is either a `Success<S>` or a `Failure<F>`. Once a `Result` has been constructed, we can then chain operations on it, preserving a failure at the point it happened:

```java
public static Breakfast makeBreakfast() {
    // I don't know whether the eggs are good or not...
    Result<Eggs, Garbage> eggs
        = fridge.areEggsOff()
        ? success(fridge.getEggs())
        : failure(new Garbage(fridge.getEggs()));

    // I'm also terrible at cooking, and can ruin eggs by burning
    // or undercooking them, but salting them isn't a problem
    Result<ScrambledEggs, Garbage> scrambledEggs
        = eggs.then(flatMap(Eggs::scramble))
        .then(map(Condiments::salt));


    // I can reliably turn bread into toast, too
    Result<Toast, Garbage> toast
        = success(bread, Garbage.class).then(map(Bread::toast));

    // I am however good enough to put the eggs on toast
    Result<ScrambledEggsOnToast, Garbage> eggsOnToast = scrambledEggs.then(combineWith(toast)).using(ScrambledEggsOnToast::new);

    return eggsOnToast.then(ifFailed(__ -> new BowlOfCornflakes()));
}
```

Operations like `map`, `flatMap` and more are provided as function suppliers. 
Fundamental operations are found in the `Results` class, and there are a variety of other, more specialised operations provided in other classes and packages.

### Validation

A `Validator` is a function which takes in a value of type `T`, and returns a `Result`.

If the validation was successful, it returns a `Success<T>` of the input value: it doesn't do any transformations.

If the validation was unsuccessful, it returns both the input value and a list of all the validation errors, in the form of a `FailedValidation<T, E>`

A `FailedValidation` contains both the item being validated and a non-empty list of errors.

Because the output of a `Validator` is a `Result`, the intention here is to use a validator as the first step in a pipeline of operations, prior to any operations which have side-effects.

Individual `Validator`s can be easily composed together into a larger single `Validator`:

```java
final Validator<Integer, String> fizzBuzzValidator 
    = compose(
        rejectIf(x -> x % 3 == 0, "Fizz!"),
        rejectIf(x -> x % 5 == 0, "Buzz!")
    );
```

`Validator`s on children or derived properties can easily be composed into a `Validator` on a parent:

```java
final Validator<Luggage, String> checkBags
    = compose(
        rejectIf(luggage -> luggage.weightInKg > 30, "Maximum baggage is 30kgs"),
        rejectIf(luggage -> luggage.contains(FRESH_FRUIT), "No exporting of fresh foodstuffs")
    )


final Validator<Passenger, String> securityControl
    = compose(
        acceptIf(Passenger::hasPassport(), "You need a passport"),
        rejectIf(Passenger::isDrunk(), "You must be sober"),
        on(Passenger::getLuggage, checkBags)
    )
```

There are many other ways of composing `Validator`s, and because a `Validator` is just a `Function<T, Stream<E>>` it's easy to add your own.
