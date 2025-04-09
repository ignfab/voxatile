package com.ignfab.minalac.generator.world;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * Represents a voxel placed in the world along its coordinates.
 *
 * @param voxel a new instance of the placed voxel.
 * @param coords the {@link WorldCoords3d} where the voxel is placed.
 */
public record PlacedVoxel(Placeable voxel, WorldCoords3d coords) {}
