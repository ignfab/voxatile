package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.parameters.common.IntegerIntervalsParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.renderers.LinearRenderer;

/**
 * Parameters for a {@link LinearRendererParams}.
 */
public class LinearRendererParams extends RendererParams {
    /**
     * Models to render (required).
     */
    public String modelType;

    /**
     * What to place (required).
     */
    public PlaceableParams place;

    /**
     * Render only when above this heightmap (optional, default render always).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String renderOnlyWhenAbove;

    /**
     * At (optional, default "0").
     */
    @JsonSetter(nulls = Nulls.SKIP)
    IntegerIntervalsParams at = new IntegerIntervalsParams(0, 0);

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType type of models to render
     * @param place what to place in world at computed positions
     */
    @ConstructorProperties({"modelType", "place"})
    public LinearRendererParams(String modelType, PlaceableParams place) {
        this.modelType = modelType;
        this.place = place;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        at.validate();
    }

    @Override
    public Renderer create(Generation generation) {
        Heightmap heightmap = null;
        if (renderOnlyWhenAbove != null && !renderOnlyWhenAbove.isBlank())
            heightmap = generation.heightmaps().get(renderOnlyWhenAbove);

        return new LinearRenderer(
            new ModelSelection(generation.models(), modelType),
            place.create(generation.world()),
            at.create(),
            heightmap
        );
    }
}
