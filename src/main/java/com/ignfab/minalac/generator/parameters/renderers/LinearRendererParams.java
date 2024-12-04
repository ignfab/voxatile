package com.ignfab.minalac.generator.parameters.renderers;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.parameters.renderers.values.FixedValueParams;
import com.ignfab.minalac.generator.parameters.renderers.values.ModelValueParams;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.renderers.LinearRenderer;

import java.beans.ConstructorProperties;

/**
 * Parameters for a {@link RoadRenderer}.
 * <p>
 * Until voxel structures are deserializable, this performs a basic voxel structure creation
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class LinearRendererParams extends RendererParams {
    /**
     * Models to render (required).
     */
    public String modelType;

    /**
     * Heightmap to place on (required).
     */
    public String heightmap;


    /**
     * What to place (required).
     */
    public PlaceableParams place;

    /**
     * Width (optional, default 1).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ModelValueParams<Integer> width = new FixedValueParams<Integer>(1);

    @JsonSetter(nulls = Nulls.SKIP)
    public int verticalOffset = 0;

    @JsonSetter(nulls = Nulls.SKIP)
    public boolean onlyAboveHeightmap = false;

    @JsonSetter(nulls = Nulls.SKIP)
    public boolean alwaysAboveHeightmap = false;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType type of models to render
     * @param heightmap name of the heightmap to place on
     * @param place what to place
     */
    @ConstructorProperties({"modelType", "heightmap", "place"})
    public LinearRendererParams(String modelType, String heightmap) {
        this.modelType = modelType;
        this.heightmap = heightmap;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (heightmap.isEmpty())
            throw new IllegalArgumentException("Heightmap name cannot be empty");
    }

    @Override
    public Renderer create(Generation generation) {
        return new LinearRenderer(
            new ModelSelection(generation.models(), modelType),
            generation.heightmaps().get(heightmap),
            place.create(generation.world()),
            width.create(),
            verticalOffset,
            onlyAboveHeightmap,
            alwaysAboveHeightmap
        );
    }
}