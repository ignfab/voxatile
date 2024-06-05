package com.ignfab.minalac.generator.utils.world2d.chunk;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * The {@code WritableChunk2d} interface represents a writable two-dimensional chunk.
 * A two-dimensional chunk is an area of the surface of the voxel world where each point (x, y) is associated with a {@code int} value.
 * Coordinates in {@code WritableChunk2d} methods are relative to the voxel world, not to the chunk.
 * An explanation of the voxel world coordinates system can be found on {@link com.ignfab.minalac.generator.utils.world2d.WorldCoords2d}.
 *
 * @see WorldCoords2d
 */
public interface WritableChunk2d {
    /**
     * Returns the bounding box of the chunk.
     *
     * @return the {@link com.ignfab.minalac.generator.utils.world2d.WorldBBox2d} associated to the chunk.
     */
    WorldBBox2d bbox();

    /**
     * Set the value at the specified coordinates.
     *
     * @param x     the x-coordinate value.
     * @param y     the y-coordinate value.
     * @param value the value to set.
     */
    void set(int x, int y, int value);

    /**
     * Set the value at the specified coordinates.
     *
     * @param coords the coordinates.
     * @param value  the value to set.
     */
    void set(WorldCoords2d coords, int value);
}
