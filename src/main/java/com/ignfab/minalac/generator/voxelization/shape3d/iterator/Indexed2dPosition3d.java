package com.ignfab.minalac.generator.voxelization.shape3d.iterator;

import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;

/**
 * @param coords Voxel position in world
 * @param index Voxel index along geometry (x is along axis, y is wide)
 */
public record Indexed2dPosition3d(WorldCoords3d coords, Vector2d index, Line2d line) implements Positioned3d {}
