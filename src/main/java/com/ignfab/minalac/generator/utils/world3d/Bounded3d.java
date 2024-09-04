package com.ignfab.minalac.generator.utils.world3d;

/**
 * Object having 3-dimensions boundaries in voxel World.
 *
 * <p>
 * A class cannot implement both {@code Bounded3d} and {@code Bounded2d}. If 2d
 * bounding box is needed for a {@code Bounded3d}, use {@code bbox().to2d()}.
 */
public interface Bounded3d {
    /**
     * Gives the bounding box of the object.
     *
     * @return the bounding box
     */
    WorldBBox3d bbox();
}
