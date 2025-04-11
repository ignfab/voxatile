package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * Represents a 2d readable heightmap (in voxel world units) bound to a generation tile.
 */
public interface ReadableHeightmap {

    /**
     * Returns the height at a specified position.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return the {@code int} height at specified position
     * @throws IndexOutOfBoundsException if position is outside the heightmap and doesn't provide default value (optional).
     */
    int get(int x, int y);

    /**
     * Returns the height at a specified position.
     *
     * @param position the position
     * @return the {@code int} height at specified position
     * @throws IndexOutOfBoundsException if position is outside the heightmap and doesn't provide default value (optional).
     */
    default int get(WorldCoords2d position) {
        return get(position.x(), position.y());
    }

    /**
     * Returns the bounding box of the heightmap.
     *
     * @return the {@link WorldBBox2d} associated to the heightmap.
     */
    WorldBBox2d bbox();
}
