package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.renderers.GroundRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

/**
 * Parameters for a {@link GroundRenderer}.
 *
 * Until voxel structures are serializable, this perform a basic voxel structure creation
 */
public class GroundRendererParams extends RendererParams {
    /**
     * Random salt for this renderer (optional, default "").
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String salt = "";

    /**
     * The name of the heightmap to use (required).
     */
    public String heightmap;

    /**
     * What to place along the heightmap (required).
     */
    public PlaceableParams place;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param heightmap the name of the heightmap to use
     * @param place what to place along the heightmap
     */
    @ConstructorProperties({"heightmap", "place"})
    public GroundRendererParams(String heightmap, PlaceableParams place) {
        this.heightmap = heightmap;
        this.place = place;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        place.validate();
        if (heightmap.isEmpty())
            throw new IllegalArgumentException("The field heightmap cannot be empty");
    }

    @Override
    public Renderer create(Generation generation) {
        return new GroundRenderer(
            generation.seed().salt(salt),
            generation.heightmaps().get(heightmap),
            place.create(generation.world())
        );
    }
}
