[![Build Status](https://travis-ci.org/unruly/control.svg?branch=master)](https://travis-ci.org/unruly/control)

## Control

co.unruly.Control is a collection of functional control-flow primitives and utilities, built around a `Result` type.

### Result

What is a `Result`? It's a representation of the outcome of an operation *which may have failed*. Kind of like `java.util.Optional`.

Like `Optional`, it wraps a value, and like `Optional`, it doesn't allow you to use that value directly. 
After all, if it's representing a failed operation, there may not *be* a value.
In order to extract a value, you have to define how to handle the failure cases.

However, an `Optional` is either *present*, and contains a value, or *absent*, conveying no information. 
A `Result`, however, is either a *success*, containing a value, or a *failure*, containing information about the failure.


#### Creating a Result from a value: Introducers

The simplest way to create a `Result` is simply to instantiate either a success or failure value, as appropriate.
For example, the following code creates a successful `Result`, followed by a failed `Result`.

```java
Result<Integer, String> firstResult = Result.success(42);
Result<Integer, String> secondResult = Result.failure("What is six times nine?");
```

There are also many common repeated patterns of operations which can fail. 
For example, you may have some code which creates `Optional`s, which you would like 
to convert to `Result`s in order to better track failure causes. 

For this, `co.unruly.control.result.Introducers` contains a selection of useful functions
which yield `Result`s:

```java
public Result<Integer, String> asResult(Optional<Integer> maybeNumber) {
    return with(maybeNumber, fromOptional(() -> "No number found. :("));    
}
```

#### Extracting a value from a Result: Resolvers

The simplest way to extract a value from a `Result` is to simply describe what to do with a failure value.
For example, the following method takes a `Result` of either an `Integer` 
or a `String` describing the failure, and returns the wrapped integer if it was a success,
or -1 if it was a failure:

```java
public Integer extractValue(Result<Integer, String> count) {
    return count.then(ifFailed(x -> -1));
}
```

Sometimes, the success and failure types are the same. In that case, you can simply
collapse both cases to a single value:

```java
public static void main(String ...args) {
    printResult(Result.success("This was a triumph!"));
    printResult(Result.failure("The cake is a lie"));
}


public static String printResult(Result<String, String> value) {
    System.out.println(value.then(collapse()));
}
```
Which outputs:
```
> This was a triumph!
> The cake is a lie
```

`ifFailed` and `collapse` can both be found in `co.unruly.control.result.Resolvers`, along with a
selection of other functions to convert `Result`s into non-`Result` values.

#### Functional API

None of the operations described above are methods on the `Result` object. 
Instead, `Result` presents only two methods: `Result.either()` and `Result.then()`.

##### Result.either()

`Result.either` takes two functions, one for the success case and one for the failure case.
If the `Result` is a success, it applies the first function to its success value and returns the result.
If it was a failure, instead it applies the second function.

```java
public static void main(String ...args) {
    printResult(Result.success(99));
    printResult(Result.failure("bananas"));
}


public static String printResult(Result<String, String> value) {
    System.out.println(value.either(
        success -> success + " bottles of beer on the wall",
        failure -> "Yes sir, we have no " + failure    
    ));
}
```

Which outputs:
```
> 99 bottles of beer on the wall
> Yes sir, we have no bananas
```

Note that the argument types of the functions must match the corresponding success
and failure types, and both functions must return the same type.

##### Result.then()

`Result.then` takes a function from a `Result` to a value, and then applies
that function to the result. The following lines of code are (other than generics inference issues) equivalent:
```java
ifFailed(x -> "Hello World").apply(result);
result.then(ifFailed(x -> "Hello World"));
```

By structuring the API like this, instead of having a fixed set of methods available, we can
create more specialised functions for domain-specific use cases and mix them seamlessly with 
standard operations, and customise how we interact with `Result`s.

The functions included in this library are all higher-order functions, returning
instances of `Function`. This includes functions to create `Result`s from non-`Result`
values - both for consistency, and compatibility with idiomatic `Stream` usage. 
As non-`Result` values don't implement `then()`, and there are both readability and
generics issues, we also include `with`.

##### HigherOrderFunctions.with()

`with()` is a function which takes a `T` and a `Function<T, R>`, and applies the function
to the provided value. This serves two purposes: it positions the argument before the function,
which is more consistent with the chaining style encouraged with `Result`, and secondly
it provides the compiler with a little more assistance when inferring generic types.

For example, the following code doesn't compile:
```java
public Result<Integer, String> asResult(Optional<Integer> maybeNumber) {
    return fromOptional(() -> "No number found. :(").apply(maybeNumber);    
}
```
This fails to compile because the type of `fromOptional` is inferred to be 
`Function<Optional<Object>, Result<Object, String>`, because the type inference 
engine can't infer the success type based on subsequent operations (namely, the type
passed to `apply()`).

However, the following code does compile:
```java
public Result<Integer, String> asResult(Optional<Integer> maybeNumber) {
    return with(maybeNumber, fromOptional(() -> "No number found. :("));    
}
```
The difference here is the types of `maybeNumber` and `fromOptional` are inferred 
simultaneously, and therefore it can properly derive both the success and failure types.

Of course, `with()` is not exclusively useful with `Result`: it's a valuable tool whenever
using higher order functions in Java.

#### Chaining Results: aka Railway Oriented Programming

One common pattern with `Result`s is to chain a sequence of operations, each taking
a `Result` and outputting a new `Result`. Usually, each operation will only act on the
result if it was a success, simply passing the existing failure through if it was a failure.