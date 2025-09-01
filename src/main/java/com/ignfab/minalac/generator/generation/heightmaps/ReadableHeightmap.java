package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * Represents a 2d readable heightmap (in voxel world units).
 * <p>
 * A heightmap is an array associating a height (integer z) to each coordinate (x, y) in a heightmap bounding box.
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
     * {@return the bounding box associated to the heightmap}
     */
    WorldBBox2d bbox();
}
