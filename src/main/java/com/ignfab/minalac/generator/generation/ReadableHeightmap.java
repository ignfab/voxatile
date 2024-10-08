package com.ignfab.minalac.generator.generation;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A 2d readable heightmap in voxel world units.
 *
 * @see Heightmap
 */
public interface ReadableHeightmap {
    /**
     * Returns the bounding box of the heightmap.
     *
     * @return the {@link WorldBBox2d} associated to the heightmap.
     */
    WorldBBox2d bbox();

    /**
     * Returns the height at a specified position.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return the {@code int} height at specified position
     * @throws IndexOutOfBoundsException if position is outside the heightmap.
     */
    int get(int x, int y);

    /**
     * Returns the height at a specified position.
     *
     * @param position the position
     * @return the {@code int} height at specified position
     * @throws IndexOutOfBoundsException if position is outside the heightmap.
     */
    default int get(WorldCoords2d position) {
        return get(position.x(), position.y());
    }
}
