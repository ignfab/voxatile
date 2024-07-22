package com.ignfab.minalac.generator.voxelization.shape3d;

import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A 3d voxel from a line with additional information about this line.
 *
 * @param line line which voxel comes from
 * @param index index of the voxel in that line
 */
public record LineVoxel3d(Line3d line, int index) implements Positioned3d {
    @Override
    public WorldCoords3d coords() {
        return line.atIndex(index);
    }
}
