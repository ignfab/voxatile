package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.tasks.SetSpawnTask;
import com.ignfab.minalac.generator.tasks.TileTask;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * Parameters for creating a {@link SetSpawnTask}.
 */
public class SetSpawnTaskParams extends SimpleTaskParams {
    /**
     * The heightmap to use for the spawn z-coordinate.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams heightmap;
    /**
     * The spawn x-coordinate (required).
     * Must be within the extent {@link com.ignfab.minalac.generator.parameters.GenerationParams#area}.
     */
    public int x;
    /**
     * The spawn y-coordinate (required).
     * Must be within the extent {@link com.ignfab.minalac.generator.parameters.GenerationParams#area}.
     */
    public int y;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param heightmap the heightmap to use
     * @param x the spawn x-coordinate
     * @param y the spawn y-coordinate
     */
    @ConstructorProperties({"heightmap", "x", "y"})
    public SetSpawnTaskParams(ReadableHeightmapParams heightmap, int x, int y) {
        this.heightmap = heightmap;
        this.x = x;
        this.y = y;
    }

    @Override
    public void validate() {
        super.validate();
        heightmap.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        WorldCoords2d spawn = new WorldCoords2d(x, y);

        if (!generation.world().limits().to2d().contains(spawn))
            throw new IllegalArgumentException("Provided spawn coordinates are outside of world limits.");

        return new SetSpawnTask(heightmap.create(generation.heightmaps()), spawn);
    }
}
