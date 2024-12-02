package com.ignfab.minalac.generator.voxelization.shape2d;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A 2d voxel from a line with additional information about this line.
 *
 * @param line line which voxel comes from
  * @param coords position of the voxel in world*
 * @param index index of the voxel in that line
 */
public record LineVoxel2d(Line2d line, WorldCoords2d coords, int index) implements Positioned2d {
    public LineVoxel2d(Line2d line, int index) {
        this(line, line.atIndex(index), index);
    }
}

