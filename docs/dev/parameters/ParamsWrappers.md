# Params Wrappers

Sometimes, an existing _params class_ almost fits a use case, but not exactly (e.g. is missing one property, should inherit from a particular class, or should come with its own validation logic). Creating a new params class for that exact use case would cause some duplication, as inheritance is not always possible.

One solution would be to use composition instead, with the new params class having a property of the type of the original class. However, it would reflect in the _YAML parameters_, resulting in an extra nesting.

The `@JsonWrapper` was created specifically for this scenario.

## Simple usage

Its most basic usage consists of annotating a params class wrapping a single property so that it is deserialized as if it was only this underlying property's type.

Example:
```java
@JsonWrapper
public class MyWrapperParams {
    // Single wrapped property written as normal
    @JsonSetter(nulls = Nulls.SKIP)
    public List<String> list = new ArrayList<>();

    public void validate() {
        // Custom validation logic
        for (String str : list)
            if (str.contains("foo"))
                throw new IllegalArgumentException("List values cannot contain 'foo'!");
    }

    public MyWrapper create() {
        return new MyWrapper(list);
    }
}
```

`MyWrapperParams` will be deserializable directly from a list of strings instead of an object with a single key `list` whose value being the list of strings.

## Multi usage

Sometimes, the target _parameters_ are the union of multiple existing ones. Similar to the simple usage, multi usage consists of annotating a params class, but this time wrapping multiple properties. Deserialization is delegated to each wrapped property, and the resulting wrapper object is created at the end.

Each property's deserializer is called on the same, complete, parameters. Unknown properties are ignored at that step to allow deserializing each underlying value even when other properties' values are also present. This means all properties must be deserialized from mappings (otherwise, union would make no sense). At the end, if a property was not used by any deserializer, it will fail the whole deserialization of the wrapper, to help detecting potential mistakes in the parameters. A property can be shared by multiple deserializers (it won't be "consumed" by the first one and disappear for the second one).

Example:
```java
// Some existing params classes
public record NamedParams(String name, List<String> aliases) {}
public record PointParams(int x, int y, int z) {}

// The new params class using multi wrapper
@JsonWrapper
public class NamedPointParams {
    // Both properties will be deserialized from the same root mapping
    public NamedParams named;
    public PointParams point;

    public NamedPoint create() {
	    return new NamedPoint(point.x, point.y, point.z, named.name, named.aliases);
    }
}
```

`NamedPointParams` could be deserialized from those parameters:
```yaml
name: Foo
aliases: [ Bar, Foobar ]
x: 7
y: -3
z: 5
```

## Direct properties

TODO...

## Null handling and custom policies

TODO...
