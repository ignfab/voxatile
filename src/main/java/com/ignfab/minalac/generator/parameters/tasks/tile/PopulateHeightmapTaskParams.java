package com.ignfab.minalac.generator.parameters.tasks.tile;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.WritableHeightmapParams;
import com.ignfab.minalac.generator.tasks.PopulateHeightmapTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link PopulateHeightmapTask}.
 */
public class PopulateHeightmapTaskParams extends ModelTaskParams {
    /**
     * The name of the heightmap to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public WritableHeightmapParams heightmap;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param heightmap the name of the heightmap to use.
     */
    @ConstructorProperties({"heightmap"})
    public PopulateHeightmapTaskParams(WritableHeightmapParams heightmap) {
        this.heightmap = heightmap;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        heightmap.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new PopulateHeightmapTask(
            models.create(),
            heightmap.create(generation.heightmaps())
        );
    }
}
