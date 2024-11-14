package com.ignfab.minalac.generator.parameters.renderers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.selection.ModelFilter;
import com.ignfab.minalac.generator.models.selection.ModelMetadataListSelection;
import com.ignfab.minalac.generator.models.selection.ModelMetadataSelection;
import com.ignfab.minalac.generator.models.selection.ModelSelection;
import com.ignfab.minalac.generator.renderers.ModelToHeightmapRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

import java.beans.ConstructorProperties;
import java.util.List;

/**
 * Concrete class of {@link RendererParams} representing the parameters of a {@link ModelToHeightmapRenderer}.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class ModelToHeightmapRendererParams extends RendererParams {
    /**
     * The type of models to render.
     * This field is required during deserialization.
     */
    public String modelType;
    /**
     * The classifications to filter (temporary, optional, defaults to none).
     */
    public List<String> classifications;
    /**
     * The name of the heightmap to use.
     * This field is required during deserialization.
     */
    public String heightmap;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType the type of models to render.
     * @param heightmap the name of the heightmap to use.
     */
    @ConstructorProperties({"modelType", "heightmap"})
    public ModelToHeightmapRendererParams(String modelType, String heightmap) {
        this.modelType = modelType;
        this.heightmap = heightmap;
    }

    /**
     * {@inheritDoc}
     */
    public void validate() throws IllegalArgumentException {
        if (modelType.isEmpty())
            throw new IllegalArgumentException("The field modelType cannot be empty");
        if (heightmap.isEmpty())
            throw new IllegalArgumentException("The field heightmap cannot be empty");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Renderer create(Generation generation) {
        ModelFilter models = new ModelSelection(generation.models(), modelType);
        if (classifications != null && !classifications.isEmpty()) {
            if (classifications.size() == 1)
                models = new ModelMetadataSelection(models, "classification", classifications.get(0));
            else
                models = new ModelMetadataListSelection(models, "classification", classifications);
        }
        return new ModelToHeightmapRenderer(
            models,
            generation.heightmaps().get(heightmap)
        );
    }
}
