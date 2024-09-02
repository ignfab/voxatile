package com.ignfab.minalac.generator.voxelization.shape2d;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A 2d voxel out of a linear geometry (usually a line or polyline) with additional information about this geometry.
 *
 * @param line line which voxel comes from
 * @param coords position of the voxel in world
 * @param index index of the voxel in that line
 */
public record LinearVoxel2d(Line2d line, WorldCoords2d coords, int index) implements Positioned2d {
    /**
     * Creates a new {@code LinearVoxel2d}.
     *
     * @param line line which voxel comes from
     * @param index index of that voxel comes from
     */
    public LinearVoxel2d(Line2d line, int index) {
        this(line, line.atIndex(index), index);
    }
}

