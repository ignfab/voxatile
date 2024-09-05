package com.ignfab.minalac.generator.utils.world3d;

/**
 * Object having 3 dimensions boundaries in voxel World.
 *
 * A class cannot implement both Bounded3d and Bounded2d. If 2d bounding box is
 * needed for a Bounded3d, use `bbox().to2d()`.
 */
public interface Bounded3d {
    /**
     * Gives the bounding box of the object.
     *
     * @return the bounding box
     */
    WorldBBox3d bbox();
}
