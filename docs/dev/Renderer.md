# How to enable deserialization for a new `Renderer`

Say we have created a `FooRenderer`. To enable the deserialization, this is what needs to be done:
1) Create a `FooRendererParams` class that extends `RendererParams` in the package `parameters.renderers`.
2) Add the parameters needed for `FooRenderer`.
3) Implements `create(Generation generation)`. The `validate()` can be implemented if needs be.

Now the `FooRendererParams` can be deserialized by registering it.

# Register a `RendererParams`
Each renderer parameter has a field `type` which is used to identify a specific renderer type during deserialization.
Prior to parsing each type of renderer has to be registered using `registerRenderer(String, Class)`.
```java
ParamsParser parser = new ParamsParser();
parser.registerRenderer("foo", FooRendererParams.class);
```
