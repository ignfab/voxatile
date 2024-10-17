package com.ignfab.minalac.generator.parameters.renderers;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.selection.ModelSelection;
import com.ignfab.minalac.generator.renderers.MetadataRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

import java.beans.ConstructorProperties;
import java.util.List;

/**
 * Concrete class of {@link RendererParams} representing the parameters of a {@link MetadataRenderer}.
 */
// Since attributes are purposely kept public for this class the checkstyle for visibility is disabled.
@SuppressWarnings("checkstyle:VisibilityModifier")
public class MetadataRendererParams extends RendererParams {
    /**
     * The type of models those metadata should be rendered.
     * This field is required during deserialization.
     */
    public String modelType;
    /**
     * The list of metadata to render.
     * This field is optional, defaults to rendering all metadata of the model.
     */
    @JsonSetter(
        nulls = Nulls.SKIP,
        // To prevent null values on required field of an element of the list.
        contentNulls = Nulls.FAIL
    )
    public List<String> metadataNames;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType the type of models to render metadata.
     */
    @ConstructorProperties({"modelType"})
    public MetadataRendererParams(String modelType) {
        this.modelType = modelType;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (modelType.isEmpty())
            throw new IllegalArgumentException("The field modelType cannot be empty");
    }

    @Override
    public Renderer create(Generation generation) {
        return new MetadataRenderer(
            new ModelSelection(generation.models(), modelType),
            generation.world().highestVoxels(),
            generation.world().getFactory(),
            metadataNames == null ? MetadataRenderer.ALL_METADATA : metadataNames
        );
    }
}
