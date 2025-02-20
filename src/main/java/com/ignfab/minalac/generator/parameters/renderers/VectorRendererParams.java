package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.renderers.VectorRenderer;

/**
 * Concrete class of {@link RendererParams} representing the parameters of a {@link VectorRenderer}.
 */
public class VectorRendererParams extends RendererParams {
    /**
     * The type of models to render (required).
     */
    public ModelSelectionParams models;
    /**
     * The name of the ground heightmap to use (required).
     */
    public String heightmap;
    /**
     * What to place inside shapes (required).
     */
    public PlaceableParams inside;
    /**
     * What to place on shapes edges (required).
     */
    public PlaceableParams edge;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models models selection to render.
     * @param heightmap the name of the ground heightmap to use.
     * @param inside what to place on inside shapes
     * @param edge what to place on shapes edges
     */
    @ConstructorProperties({"models", "heightmap", "inside", "edge"})
    public VectorRendererParams(ModelSelectionParams models, String heightmap, PlaceableParams inside, PlaceableParams edge) {
        this.models = models;
        this.heightmap = heightmap;
        this.inside = inside;
        this.edge = edge;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (heightmap.isEmpty())
            throw new IllegalArgumentException("The field heightmap cannot be empty");
        inside.validate();
        edge.validate();
        models.validate();
    }

    @Override
    public Renderer create(Generation generation) {
        return new VectorRenderer(
            models.create(generation.models()),
            generation.heightmaps().get(heightmap),
            inside.create(generation.world()),
            edge.create(generation.world())
        );
    }
}
