# User-Typed Values

When a value in parameters needs to have a user-defined type, you can use the `ValueParser` class.

## Table of contents

* [Reading a user-typed value from the parameters](#reading-a-user-typed-value-from-the-parameters)
* [Reading a type from the parameters to parse other values](#reading-a-type-from-the-parameters-to-parse-other-values)
* [Built-in parsers](#built-in-parsers)
* [Adding custom parsers](#adding-custom-parsers)

## Reading a user-typed value from the parameters

For example, let's say that `MyStuffParams` must accept an arbitrarily-typed constant value.
In the params class, we set the type of the value to `Object`, and add a parameter of type `ValueParser<?>` which will represent the type of that value.

```java
public class MyStuffParams {
    public Object value;
    public ValueParser<?> as;
    // Other parameters...

    public MyStuff create() {
        return new MyStuff(
            // Parse value according to `as`:
            as.parse(value)
            // ...
        );
    }
}
```
Note: The value parser will try its best to parse the value, but it is not possible to verify whether the value is parseable or not before actually trying to parse it. This means that the value should not be validated in `.validate()`, to avoid double parsing.

In the YAML parameters, the user will be able to write, for example:
```yaml
myStuff:
  value: 123
  as: decimal
  # Other parameters...
```

## Reading a type from the parameters to parse other values

TODO...

## Built-in parsers

Built-in value parsers are:

| Name      | Java class |
| --------- | ---------- |
| `integer` | `Integer`  |
| `decimal` | `Double`   |
| `text`    | `String`   |
| `boolean` | `Boolean`  |

## Adding custom parsers

More parsers can be registered using `ValueParser#register` method.

TODO...
