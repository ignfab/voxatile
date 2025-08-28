package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A 2d voxel position (in world) with geometry indexes and distances.
 *
 * @param coords Voxel position in world
 * @param index Index of nearest point in geometry
 * @param distance Distance to nearest point in geometry (could be negative depending on which side)
 */
public record IndexedPosition2d(WorldCoords2d coords, double index, double distance) implements Positioned2d {
    /**
     * Creates an IndexedPosition2d out of a Positioned2d.
     *
     * @param positioned the positioned object
     * @param index Index of nearest point in geometry
     * @param distance Distance to nearest point in geometry (could be negative depending on which side)
     */
    public IndexedPosition2d(Positioned2d positioned, double index, double distance) {
        this(positioned.coords(), index, distance);
    }
}
