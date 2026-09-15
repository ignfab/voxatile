package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * A placeable made of other placeables.
 * <p>
 * Each underlying placeable has a unique position.
 * There is a finite number of underlying placeables.
 */
public interface Structure extends Placeable {
    /**
     * Gets placeable at the given position in the structure.
     * <p>
     * (0, 0, 0) is the point that will be placed in the world at {@link Placeable#place} position.
     *
     * @param x x-component of position
     * @param y y-component of position
     * @param z z-component of position
     *
     * @return placeable at that position or {@link Nothing} if none.
     */
    Placeable get(int x, int y, int z);

    /**
     * Limits of this structure.
     * <p>
     * Outside this limit, {@link Structure#get(int, int, int)} will always return {@link Nothing}.
     * <p>
     * Limits are distinct from bounding box as limits only includes direct underlying placeables.
     * A hierarchy of structures could place voxels beyond these limits.
     *
     * @return limits of the placeable
     */
    WorldBBox3d limits();
}
