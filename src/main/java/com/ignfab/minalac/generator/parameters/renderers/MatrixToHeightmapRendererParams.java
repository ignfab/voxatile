package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.StoredHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.renderers.MatrixToHeightmapRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

/**
 * Represents the parameters of a {@link MatrixToHeightmapRenderer}.
 */
public class MatrixToHeightmapRendererParams extends RendererParams {
    /**
     * The type of models to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;
    /**
     * The name of the heightmap to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public StoredHeightmapParams heightmap;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models models selection to render.
     * @param heightmap the name of the heightmap to use.
     */
    @ConstructorProperties({"models", "heightmap"})
    public MatrixToHeightmapRendererParams(ModelSelectionParams models, StoredHeightmapParams heightmap) {
        this.models = models;
        this.heightmap = heightmap;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        heightmap.validate();
        models.validate();
    }

    @Override
    public Renderer create(Generation generation) {
        return new MatrixToHeightmapRenderer(
            models.create(generation.models()),
            heightmap.create(generation.heightmaps())
        );
    }
}
