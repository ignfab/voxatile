package com.ignfab.minalac.generator.parameters.renderers;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.renderers.Renderer;

/**
 * Represents the parameters of a type of {@link Renderer}.
 * The concrete class is resolved by using the value of the {@link RendererParams#type}.
 * Mapping a value with a class can be done by using {@link com.ignfab.minalac.generator.parameters.ParamsParser#registerRenderer(String, Class)}.
 */
@JsonTypeInfo(
    // name id used by jackson to map implementation classes. The id is created by jackson if not provided.
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
// Since attributes are purposely kept public for this class the checkstyle for visibility is disabled.
@SuppressWarnings("checkstyle:VisibilityModifier")
public abstract class RendererParams {
    // This property isn't deserialized.
    // If deserialization is needed, fields `include = JsonTypeInfo.As.EXISTING_PROPERTY` and `visible = true` should be added.
    /**
     * This field is required.
     * It is not deserialized but used to find out the concrete class of the renderer.
     */
    public String type;

    /**
     * Checks if there are any blatantly invalid parameters.
     *
     * @throws IllegalArgumentException is any of the parameters is invalid.
     */
    public void validate() throws IllegalArgumentException {}

    /**
     * Creates the corresponding {@code Renderer}.
     *
     * @param generation the generation context.
     * @return the corresponding renderer
     */
    public abstract Renderer create(Generation generation);
}
