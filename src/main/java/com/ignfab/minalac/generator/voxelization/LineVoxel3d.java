package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.utils.shape3d.Line3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A 3d voxel from a line with additional information about this line.
 *
 * @param coords coordinates of the voxel
 * @param line line voxel comes from
 * @param index index of the voxel in the line
 */
public record LineVoxel3d(WorldCoords3d coords, Line3d line, int index) implements Voxel3d {
}
