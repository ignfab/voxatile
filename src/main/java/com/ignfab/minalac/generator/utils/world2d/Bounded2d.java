package com.ignfab.minalac.generator.utils.world2d;

/**
 * Object having 2-dimensions boundaries in voxel World.
 */
public interface Bounded2d {
    /**
     * {@return the bounding box of the object}
     */
    WorldBBox2d bbox();
}
