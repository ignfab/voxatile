package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.utils.shape2d.Line2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A 2d voxel from a line with additional information about this line.
 *
 * @param coords coordinates of the voxel
 * @param line line voxel comes from
 * @param index index of the voxel in the line
 */
public record LineVoxel2d(WorldCoords2d coords, Line2d line, int index) implements Voxel2d {
}
