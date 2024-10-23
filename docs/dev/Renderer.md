# Renderers cookbook

## Deserialization from Yaml/Json

To make a new renderer deserializable:
1. [Create a parameter class](#create-a-parameter-class);
2. [Register it](#register-parameter-class).

### Create a parameter class

A renderer parameter class is named after renderer name. For example, parameter class for `FooRenderer` should be named `FooRendererParams`.
It should extend `RendererParam` class and belong to `parameters.renderers` package.

Fields can be added as public members of that class. Required fields are passed as constructor arguments and marked with `@ConstructorProperties` annotation.

Required `create()` method should create a `FooRenderer` out of parameters.

Optional `validate()` method can perform some more checks that *Jackson* could not do.

*Example of parameter class implementation:*
```java
package com.ignfab.minalac.generator.parameters.renderers;
...

public class FooRendererParams extends RendererParams {

    public PlaceableParams placeable;
    public int required;
    public Integer optional; // As this field may be null, it cannot be typed as int

    @ConstructorProperties({"placeable", "required"})
    public HeightmapRendererParams(PlaceableParams placeable, int required) {
        this.placeable = placeable;
        this.required = required;
    }

    @Override
    public void validate() {
        placeable.validate(); // You must validate your sub-params
    }

    @Override
    public FooRenderer create(Generation generation) {
        // Create renderer using parameters
        return new FooRenderer(placeable.create(generation.world()), required, optional);
    }
}
```

You may use sub parameter objects as fields. In that case, you have to call their `validate()` method in yours (and of course, their `create()` method in yours).

In the above example, `placeable` field is validated in `FooRendererParams.validate()` method.

### Register parameter class

Registration consists in making new renderer available in Yaml/Json parameter file. It is associated with a type name using `registerParams()` method of `ParamsParser` used for file parsing.

*Example of renderer registration:*

```java
ParamsParser parser = new ParamsParser();
...

/// Register FooRenderer with "foo" type
parser.registerParams("foo", FooRendererParams.class);
...

Generation generation = parser.parse(...).create();
```
