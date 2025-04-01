# Tile tasks cookbook

Tile tasks are tasks performed on each map tile. They are described by classes extending `TileTask` or `ModelTask` if they process models one by one. All work in generator is done by different tasks.

## Deserialization from Yaml/Json

To make a new task deserializable:
1. [Create a parameter class](#create-a-parameter-class);
2. [Register it](#register-parameter-class).

### Create a parameter class

A task parameter class is named after task name. For example, parameter class for `FooTask` should be named `FooTaskParams`.
Parameters (for `TileTask` or `ModelTask`) should extend `TileTaskParam` class and belong to `parameters.tasks` package.

Fields can be added as public members of that class. Required fields are passed as constructor arguments and marked with `@ConstructorProperties` annotation.

Required `create()` method should create a `FooTask` out of parameters.

Optional `validate()` method can perform some more checks that *Jackson* could not do.

*Example of parameter class implementation:*
```java
package com.ignfab.minalac.generator.parameters.tasks;
...

public class FooTaskParams extends TileTaskParam {

    public PlaceableParams placeable;
    public int required;
    public Integer optional; // As this field may be null, it cannot be typed as int

    @ConstructorProperties({"placeable", "required"})
    public FooTaskParams(PlaceableParams placeable, int required) {
        this.placeable = placeable;
        this.required = required;
    }

    @Override
    public void validate() {
        placeable.validate(); // You must validate your sub-params
    }

    @Override
    public FooTask create(Generation generation) {
        // Create task using parameters
        return new FooTask(placeable.create(), required, optional);
    }
}
```

You may use sub parameter objects as fields. In that case, you have to call their `validate()` method in yours (and of course, their `create()` method in yours).

In the above example, `placeable` field is validated in `FooTaskParams.validate()` method.

### Register parameter class

Registration consists in making new task available in Yaml/Json parameter file. It is associated with a type name using `registerParams()` method of `ParamsParser` used for file parsing.

*Example of task registration:*

```java
ParamsParser parser = new ParamsParser();
...

/// Register FooTaskParams with "foo" type
parser.registerParams("foo", FooTaskParams.class);
...

Generation generation = parser.parse(...).create();
```
