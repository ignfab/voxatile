package com.ignfab.minalac.generator.utils.world2d;

/**
 * Object having 2-dimensions boundaries in voxel World.
 */
public interface Bounded2d {
    /**
     * Gives the bounding box of the object.
     *
     * @return the bounding box
     */
    WorldBBox2d bbox();
}
