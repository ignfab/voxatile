package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;

/**
 * @param coords Voxel position in world
 * @param index Voxel index along geometry (x is long, y is wide)
 */
public record IndexedPosition2d(WorldCoords2d coords, Vector2d index, Line2d line) implements Positioned2d {}
