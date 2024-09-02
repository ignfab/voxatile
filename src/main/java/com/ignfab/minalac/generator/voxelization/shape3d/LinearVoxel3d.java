package com.ignfab.minalac.generator.voxelization.shape3d;

import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.Vector3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A 3d voxel out of a linear geometry (usually a line or polyline) with additional information about this geometry.
 *
 * @param coords position of the voxel in world
 * @param slope slope direction
 * @param nextCoords next voxel position in the world
 * @param nextSlope next voxel slope direction
 * @param index index of the voxel (usualy in a line)
 */
public record LinearVoxel3d(
    WorldCoords3d coords,
    Vector3d slope,
    WorldCoords3d nextCoords,
    Vector3d nextSlope,
    int index) implements Positioned3d {
}
