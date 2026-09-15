# Deserializers

_Deserializers_ interpret encoded parameters into Java objects. In Voxatile, they are used to automatically instantiate [params classes][PARAMS] from [YAML parameters][PARAMS], and are implemented through the Jackson library.

[PARAMS]: Parameters.md#parameters-params-classes-and-regular-classes

Jackson provides a set of built-in deserializers with various purposes. See the [`ValueDeserializer` class](https://javadoc.io/doc/tools.jackson.core/jackson-databind/latest/tools.jackson.databind/tools/jackson/databind/ValueDeserializer.html) and its hierarchy tree to find them.

To support more evolved syntaxes for parameters, built-in deserializers might not be enough. In this case, and if no existing solution can help, it is possible to write a custom deserializer.

## Table of contents

* [Verifying if a custom deserializer is really needed](#verifying-if-a-custom-deserializer-is-really-needed)
* [Writing a custom deserializer](#writing-a-custom-deserializer)
* [Writing a delegating deserializer](#writing-a-delegating-deserializer)
* [Knowing if deserialization happens with or without type](#knowing-if-deserialization-happens-with-or-without-type)

## Verifying if a custom deserializer is really needed

Before starting writing a custom deserializer, it might be worth it to verify in this list of situations to see if a solution already exists.

- **Allowing either a single value or a list of values (on a property)**:

  Make the type of the property a list of values and annotate it with `@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)`.

  See `TaskParams#after` for example.


- **Deserializing from another type only (such as a list or a map)**:

  Put a single property of the desired type with any name (won't be visible in the parameters) and annotate the params class with `@JsonWrapper`.

  See `ScheduleParams` for example.

  Read more about [params wrappers](ParamsWrappers.md).


- **Deserializing from another type with extra properties**:

  Put a property of the desired type with any name (won't be visible in the parameters), add the extra properties, annotated with `@JsonWrapper.DirectProperty` and annotate the params class with `@JsonWrapper`.

  Read more about [params wrappers](ParamsWrappers.md).


- **Reading a user-typed value**:

  Make the type of the property `Object` and add a property of type `ValueParser<?>` with a meaningful name (such as `as`), when use it to parse the value at _creation_-time.

  See `MetadataDefaultPostProcessorParams` for example.

  Read more about [user-typed values in parameters](UserTypedValues.md).

Here are also some known situations where a custom deserializer is needed. If they are encountered too much, it might be interesting to add a proper tool for it.

- **Having an implementation handling a list of instances**:

  A delegating deserializer is required.

  See `PostProcessorParams.Deserializer` for example.


- **Looking up a value from a string key in a map**:

  A deserializer is required.

  See `ValueParser.Deserializer` for example.

## Writing a custom deserializer

A custom deserializer is a class inheriting from `ValueDeserializer<T>` (with `T` being the target type).
Technically speaking, the only method required is `#deserialize()`. However, implementing `#deserializeWithType()` might be necessary, as explained [below](#knowing-if-deserialization-happens-with-or-without-type). Additionally, other methods such as `#getNullValue()` can be useful.

There are two ways to perform actual deserialization. The easiest is to read the whole value as a tree using `JsonParser#readValueAsTree()`, then use that tree to deserialize the object. The other is to read tokens sequentially using `JsonParser#nextToken()`, which might be trickier depending on the goal.

The `DeserializationContext` object contains a lot of useful methods to deserialize other values and handle errors.

Example:
```java
public class MyDeserializer extends ValueDeserializer<MyTypeParams> {
    @Override
    public MyTypeParams deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        JsonNode node = parser.readValueAsTree();

        // Simple case with a string
        if (node.isString())
            return new MyTypeParams(node.asString());

        // Another case with an array
        if (node.isArray()) {
            List<String> list = new ArrayList<>(node.size());
            for (JsonNode text : node)
                list.add(text.asString());
            return new MyTypeParams(String.join("", list));
        }

        // Complex case with an object
        if (node.isObject()) {
            JsonNode value = node.get("value");
            if (value == null)
                return context.reportPropertyInputMismatch(MyTypeParams.class, "value", "Missing 'value' property");
            JsonNode repeat = node.get("repeat");
            return new MyTypeParams(value.asString().repeat(repeat == null ? 1 : repeat.intValue()));
        }

        // Otherwise fail
        return context.reportInputMismatch(MyTypeParams.class, "Value must be a string, an array, or an object");
    }
}
```

To associate the deserializer class to the target params class, the annotation `@JsonDeserialize(using = MyDeserializer.class)` must be placed on the target params class.

## Writing a delegating deserializer

Delegating deserializers are a variant of deserializers relying on another deserializer to do (part of) the actual deserialization. Those are particularly useful for example when extending parameters syntax without replacing the default one. In such case, the delegating deserializer would handle the special syntax if detected, and delegate to the default one otherwise.

A delegating deserializer is a class inheriting from `DelegatingDeserializer` (which is untyped).
It must have a single-arg constructor accepting the deserializer to delegate to, and implement the `#newDelegatingInstance()` method calling that constructor.
Deserialization methods can be overridden to achieve desired effect, considering that calling `super` will delegate.

Example:
```java
public class MyDelegatingDeserializer extends DelegatingDeserializer {
    public Deserializer(ValueDeserializer<?> delegate) {
        super(delegate);
    }

    @Override
    protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> delegate) {
        return new MyDelegatingDeserializer(delegate);
    }

    @Override
    public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer) {
        return switch (parser.currentToken()) {
            // Special cases for integer and string
            case VALUE_NUMBER_INT -> new MyIntegerValueParams(parser.getIntValue());
            case VALUE_STRING -> new MyStringValueParams(parser.getString());
            // Delegates to default deserialization otherwise
            default -> super.deserializeWithType(parser, context, typeDeserializer);
        };
    }
}
```

To associate the delegating deserializer class to the target params class, the annotation `@JsonDelegateDeserialize(using = MyDelegatingDeserializer.class)` must be placed on the target params class.

## Knowing if deserialization happens with or without type

When implementing a custom or delegating deserializer, either `#deserialize()` or `#deserializeWithType()` is required (very rarely both).
The former is called when the concrete type of the value is known, for example with simple values (like `HeightmapDeclarationParams`).
The latter is called when only the abstract type is known, and type ID or deduction will be performed to find the actual type to deserialize, for example with hierarchies (like `TaskParams`).
