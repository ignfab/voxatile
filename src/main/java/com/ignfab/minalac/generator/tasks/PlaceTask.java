package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * This task places something somewhere.
 * */
public class PlaceTask implements TileTask {
    private final Placeable placeable;
    private final WorldCoords3d pos;

    /**
     * Creates a new {@code PlaceTask}.
     *
     * @param placeable what to place
     * @param pos where to place
     */
    public PlaceTask(Placeable placeable, WorldCoords3d pos) {
        this.placeable = placeable;
        this.pos = pos;
    }

    @Override
    public void run(GenerationTile tile) {
        placeable.place(tile.voxels(), pos);
    }
}
