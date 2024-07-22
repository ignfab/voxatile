package com.ignfab.minalac.generator.utils.world2d;

/**
 * Object having a 2-dimensions position in voxel world.
 */
public interface Positioned2d {
    /**
     * The position of the object in voxel world.
     *
     * @return the voxel coordinate.
     */
    WorldCoords2d coords();
}
