package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.renderers.RoadRenderer;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.Placeable;
import com.ignfab.minalac.generator.world.SimpleVoxelStructure;

/**
 * Parameters for a {@link RoadRenderer}.
 * <p>
 * Until voxel structures are deserializable, this performs a basic voxel structure creation
 */
public class RoadRendererParams extends RendererParams {
    /**
     * The models to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;
    /**
     * The name of the heightmap to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String heightmap;
    /**
     * Whether to create a large road (temporary, to be improved using real width).
     */
    public boolean large;
    /**
     * What to place along the road (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models the models to render
     * @param heightmap the name of the heightmap to use
     * @param place placeable to place
     */
    @ConstructorProperties({"models", "heightmap", "place"})
    public RoadRendererParams(ModelSelectionParams models, String heightmap, PlaceableParams place) {
        this.models = models;
        this.heightmap = heightmap;
        this.place = place;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (heightmap.isEmpty())
            throw new IllegalArgumentException("The field heightmap cannot be empty");
        models.validate();
        place.validate();
    }

    @Override
    public Renderer create(Generation generation) {
        SimpleVoxelStructure structure = new SimpleVoxelStructure();
        Placeable placeable = place.create(generation.world());
        if (large) {
            structure.set(new WorldBBox3d(-1, -2, 0, 3, 1, 1), placeable);
            structure.set(new WorldBBox3d(-2, -1, 0, 5, 3, 1), placeable);
            structure.set(new WorldBBox3d(-1, +2, 0, 3, 1, 1), placeable);
        } else {
            structure.set(new WorldBBox3d(-1, 0, 0, 3, 1, 1), placeable);
            structure.set(new WorldBBox3d(0, -1, 0, 1, 3, 1), placeable);
        }

        return new RoadRenderer(
            models.create(generation.models()),
            generation.heightmaps().get(heightmap),
            structure
        );
    }
}
