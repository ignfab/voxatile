package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.renderers.LinearRenderer;

/**
 * Parameters for a {@link LinearRendererParams}.
 */
public class LinearRendererParams extends RendererParams {
    /**
     * Models to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    /**
     * What to place (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableStructureParams structure;

    /**
     * Render only when above this heightmap (optional, default render always).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String renderOnlyWhenAbove;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *e
     * @param models selection of models to render
     * @param place what to place in world at computed positions
     */
    @ConstructorProperties({"models", "structure"})
    public LinearRendererParams(ModelSelectionParams models, PlaceableStructureParams structure) {
        this.models = models;
        this.structure = structure;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        structure.validate();
        models.validate();
    }

    @Override
    public Renderer create(Generation generation) {
        Heightmap heightmap = null;
        if (renderOnlyWhenAbove != null && !renderOnlyWhenAbove.isBlank())
            heightmap = generation.heightmaps().get(renderOnlyWhenAbove);

        return new LinearRenderer(
            models.create(generation.models()),
            structure.create(generation.world()),
            heightmap
        );
    }
}
