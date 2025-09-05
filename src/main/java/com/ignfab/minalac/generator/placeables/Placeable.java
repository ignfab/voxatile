package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * The {@code Placeable} interface represents something placeable in voxel world.
 */
public interface Placeable {
    /**
     * Places the placeable at given position in {@link GenerationTile}.
     * Coordinates are expressed using the system used in {@link WorldCoords3d}.
     *
     * @param tile tile to place into
     * @param x position x-coordinate
     * @param y position y-coordinate
     * @param z position z-coordinate
     */
    void place(GenerationTile tile, int x, int y, int z);

    /**
     * Places the placeable at given position in {@link GenerationTile}.
     *
     * @param tile tile to place into
     * @param position position where to place the placeable
     */
    default void place(GenerationTile tile, WorldCoords3d position) {
        place(tile, position.x(), position.y(), position.z());
    }
}
