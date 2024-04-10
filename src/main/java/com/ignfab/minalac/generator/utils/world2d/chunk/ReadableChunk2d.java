package com.ignfab.minalac.generator.utils.world2d.chunk;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * The {@code ReadableChunk2d} interface represents a readable two-dimensional chunk.
 * A two-dimensional chunk is an area of the surface of the voxel world where each point (x, y) is associated with a {@code int} value.
 * Coordinates in {@code ReadableChunk2d} methods are relative to the voxel world, not to the chunk.
 * An explanation of the voxel world coordinates system can be found on {@link com.ignfab.minalac.generator.utils.world2d.WorldCoords2d}.
 *
 * @see WorldCoords2d
 */
public interface ReadableChunk2d {
    /**
     * Returns the bounding box of the chunk.
     *
     * @return the {@link com.ignfab.minalac.generator.utils.world2d.WorldBBox2d} associated to the chunk.
     */
    WorldBBox2d bbox();

    /**
     * Returns the value at coordinates.
     *
     * @param x the x-coordinate value.
     * @param y the y-coordinate value.
     * @return the {@code int} value at the coordinate (x, y).
     */
    int get(int x, int y);

    /**
     * Returns the value at coordinates.
     *
     * @param coords the coordinates.
     * @return the {@code int} value at the provided coordinates.
     */
    int get(WorldCoords2d coords);
}
