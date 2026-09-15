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

Sometimes, a value that does not come from the parameters should be parsed into a used-defined type.
The easiest way to achieve this is to take a `ValueParser<?>` in the parameters.

It is similar to what is described for the `as` field in the previous section.

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

The `ValueParser<T>` class is not extendable. It is a `record` wrapping the target type (`Class<T>`) and a parsing function (`Function<Object, ? extends T>`).
When the parsing logic is trivial, the parser can be created inline:
```java
ValueParser<MyType> myParser = new ValueParser<>(MyType.class, MyType::createFromObject);
```

Otherwise, if the logic requires lots of code, it might be preferable to create a class:
```java
public class MyTypeParser implements Function<Object, MyType> {
    public static final ValueParser<MyType> INSTANCE = new ValueParser<>(MyType.class, new MyTypeParser());

    public MyType apply(Object obj) {
        // Parsing logic here...
        return new MyType(obj);
    }
}
```
Note: Parsing function should try to be as permissive as possible regarding the accepted input type, for example by calling `Object#toString` on it instead of restricting to a string.

Either way, the parser **must** then be registered with a name to be usable from YAML parameters:
```java
public class MyModule extends Module {
    @Override
    public void registerParams(ParamsParser parser) {
        myParser.register("myType");
        // or
        MyTypeParser.INSTANCE.register("myType");
    }
}
```

The same parser can be registered multiple times with different names to provide aliases.
However, the same name **cannot** be used multiple times!
