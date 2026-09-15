# Deserializers

_Deserializers_ interpret encoded parameters into Java objects. In Voxatile, they are used to automatically instantiate [params classes][PARAMS] from [YAML parameters][PARAMS], and are implemented through the Jackson library.

Jackson provides a set of built-in deserializers with various purposes. See the [`ValueDeserializer` class](https://javadoc.io/doc/tools.jackson.core/jackson-databind/latest/tools.jackson.databind/tools/jackson/databind/ValueDeserializer.html) and its hierarchy tree to find them.

To support more evolved syntaxes for parameters, built-in deserializers might not be enough. In this case, and if no existing tool can help, it is possible to write a custom deserializer.

## Verifying if a custom deserializer is really needed

Before starting writing a custom deserializer, it might be worth it to verify in this list of situations to see if a solution already exists.

- **Allowing either a single value or a list of values (on a property)**:
  Make the type of the property a list of values and annotate it with `@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)`.

  See `TaskParams#after` for example.

- **Deserializing from another type only (such as a list or a map)**:
  Put a single property of the desired type with any name (won't be visible in the parameters) and annotate the params class with `@JsonWrapper`.

  See `ScheduleParams` for example.

- **Deserializing from another type with extra properties**:
  Put a property of the desired type with any name (won't be visible in the parameters), add the extra properties, annotated with `@JsonWrapper.DirectProperty` and annotate the params class with `@JsonWrapper`.

- **Reading a user-typed value**:
  Make the type of the property `Object` and add a property of type `ValueParser<?>` with a meaningful name (such as `as`), when use it to parse the value at _creation_-time.

  See `MetadataDefaultPostProcessorParams` for example.

Here are also some known situations where a custom deserializer is needed. If they are encountered too much, it might be interesting to add a proper tool for it.

- **Having an implementation handling a list of instances**:
  A delegating deserializer is required.

  See `PostProcessorParams.Deserializer` for example.

- **Looking up a value from a string key in a map**:
  A deserializer is required.

  See `ValueParser.Deserializer` for example.

## Writing a delegating deserializer

TODO...

## Writing a custom deserializer

TODO...

## Knowing if deserialization happens with or without type

When implementing a custom or delegating deserializer, either `#deserialize()` or `#deserializeWithType()` is required (very rarely both).
The former is called when the concrete type of the value is known, for example with simple values (like `HeightmapDeclarationParams`).
The latter is called when only the abstract type is known, and type ID or deduction will be performed to find the actual type to deserialize, for example with hierarchies (like `TaskParams`).

[PARAMS]: Parameters.md#parameters-params-classes-and-regular-classes
