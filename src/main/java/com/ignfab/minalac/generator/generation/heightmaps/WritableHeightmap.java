package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * Represents a 2d readable and writable heightmap.
 *
 * @see ReadableHeightmap
 */
public interface WritableHeightmap extends ReadableHeightmap {

    /**
     * Sets the height at a specified position.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @param height the height to set at specified position
     * @throws IndexOutOfBoundsException if position is outside the heightmap.
     */
    void set(int x, int y, int height);

    /**
     * Sets the height at a specified position.
     *
     * @param position the position
     * @param height the height to set at specified position
     * @throws IndexOutOfBoundsException if position is outside the heightmap.
     */
    default void set(WorldCoords2d position, int height) {
        set(position.x(), position.y(), height);
    }

    /**
     * Creates a copy of this heightmap.
     *
     * @return a copy of this heightmap.
     */
    WritableHeightmap copy();

    /**
     * Copy values from a {@link ReadableHeightmap} into this one.
     * <p>
     * If bounding boxes does not match, values will be copied only over intersection of bounding boxes.
     * Values outside this intersection are left unchanged.
     *
     * @param other Heightmap to copy values from
     */
    void copyValues(ReadableHeightmap other);
}
