# JUnit

We use [JUnit](https://junit.org/junit5/) as a unit testing framework. If you're already familiar with this tool, skip to the [Usage](#Usage) section.

## What is a unit test?

A unit test is a small set of instructions whose goal is to assert a unit of code works, hence the name "unit test". Usually, a unit of code is a function, or a part of it. The main idea when creating unit tests is to ensure the code do what it claims to do.

When the tested code simply encapsulate a call to another function (either from an external library, or another part of our code), the test should just make sure it effectively calls this function, and handles correctly inputs / outputs / exceptions. Ideally, this is achieved by "mocking" the underlying function, but this implies a lot of constraints on the tested code, and most time we don't do this. However, this does not mean we should be testing that the underlying function works correctly! This is already done by the external library, or another unit test dedicated to this function.

## What is JUnit?

JUnit is a unit testing framework. It consists of an API, called Jupiter, and some external tools to run and report results.

### Structure

Tests are written inside the test root (`src/test/java/`) with the same package structure as the tested code. Append `Test` to the class name to get the test class name (e.g. `MyClass` => `MyClassTest`). Prepend `test` to the method name to get the test method name (e.g. `myMethod` => `testMyMethod`).

Test methods are annotated `@Test` and you can give a human description using the `@DisplayName("...")` annotation.

Sometimes, splitting tests into multiple methods may help. In this situation, append a descriptor to the test method name to differentiate test cases (e.g. having `testMyMethodPositiveInput` and `testMyMethodNegativeOrZeroInput`), especially when testing overloaded methods.

### Status

A test has 4 possible states:
- Success: It was executed with no error. This is what we want.
- Failure: An `AssertionFailedError` was thrown. This means the test code is valid, but the tested code is wrong.
- Error: Any other exception was thrown. This means the test code is either invalid, or an unexpected exception was thrown (there probably is another test failing somewhere that should be address to fix this ghost error).
- Skipped: This test was not executed, due to some options when running them or a condition not being met (e.g. the test is intended to only run on Windows and you are on Unix).

### Assertions

Knowing this, we want to fail our tests when something unexpected happens. The JUnit Jupiter API has **a lot** of methods to help us to do so, located inside the `org.junit.jupiter.api.Assertions` class. Usually, we import them all at the beginning of our test file:
```java
import static org.junit.jupiter.api.Assertions.*;
```

All the methods starts with `assert`, followed by a word or more about what it asserts. Example:
```java
assertTrue(boolean condition);
assertNull(Object actual);
assertEquals(Object expected, Object actual);
assertThrows(Class<? extends Throwable> expectedType, Executable executable);
assertDoesNotThrow(Executable executable);
```

Choose the one that best fits your needs. If none really applies, and you want to specify test failure in a specific case (e.g. the `default` branch of a `switch` expression), you can call the `fail()` method. All the assertion methods optionally take an additional `String message` argument, displayed in case of failure, along with extra information about expected and actual value, if applicable.

## Usage

We try to test as much code as possible. However, we prioritize the redaction of unit tests for complex code, public / reusable elements, and tools (`utils` package). We do not seek a 100% test coverage.

When adding elements that do meet the above criteria, or you think are worth testing, you should write the unit tests at the same time. Writing unit tests should be a way for you to test your code even during the development phase. Do not forget to check if the existing tests of elements you modified are still complete!

To check unit tests pass, we use the `maven-surefire-plugin` to call JUnit tests and report results during the [`test` Maven phase](Maven.md#run-unit-tests). A [GitHub workflow](GitHub-workflows.md#unit-tests) also runs automatically all unit tests on push and fails if some do not pass.

> [!TIP]
> Unit tests are also subject to the same [code-style rules](Checkstyle.md) as the source code.
> While you should try your best to adhere to it, some rules can be quite tedious, especially the `ParenPad` one (it may be considered better to align multiple consecutive assertions working together). See how to disable them for your test case in [item 2. here](Checkstyle.md#tuning-rules).
