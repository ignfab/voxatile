package com.ignfab.minalac.generator.voxelization.shape2d;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A 2d voxel from a line with additional information about this line.
 *
 * @param line line which voxel comes from
 * @param index index of the voxel in that line
 */
public record LineVoxel2d(Line2d line, int index) implements Positioned2d {
    @Override
    public WorldCoords2d coords() {
        return line.atIndex(index);
    }
}
