package com.ignfab.minalac.generator.parameters.renderers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.ModelSelection;
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
     * This field is required during deserialization.
     */
    public List<String> metadataNames;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType the type of models to render metadata.
     * @param metadataNames the names of the metadata.
     */
    @ConstructorProperties({"modelType", "metadataNames"})
    public MetadataRendererParams(String modelType, List<String> metadataNames) {
        this.modelType = modelType;
        this.metadataNames = metadataNames;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (modelType.isEmpty())
            throw new IllegalArgumentException("The field modelType cannot be empty");
        if (metadataNames.isEmpty())
            throw new IllegalArgumentException("The field metadataNames cannot be an empty list");
    }

    @Override
    public Renderer create(Generation generation) {
        return new MetadataRenderer(
            new ModelSelection(generation.models(), modelType),
            generation.world().highestVoxels(),
            generation.world().getFactory(),
            metadataNames
        );
    }
}
