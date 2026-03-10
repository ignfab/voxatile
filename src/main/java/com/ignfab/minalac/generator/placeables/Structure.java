package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public interface Structure extends Placeable {
    Placeable get(int x, int y, int z);

    /**
     * Limits of this structure in self reference coordinates.
     * <p>
     * For all voxel placeables (voxels, patterns), limits consists in one voxel at 0, 0, 0.
     * For more complex structures they can be any {@link WorldBBox3d} containing all sub-placeables.
     * <p>
     * Limits are distinct from bounding box as limits only includes placeable and its direct component.
     * A hierarchy of structure could place voxel out limits.
     *
     * @returns limits of the placeable
     */
    WorldBBox3d limits();
}
