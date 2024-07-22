package com.ignfab.minalac.generator.utils.world3d;

/**
 * Object having a 3-dimensions position in voxel world.
 *
 * <p>
 * A class cannot implement both {@code Positioned3d} and {@code Positioned2d}.
 * If 2d position is needed for a {@code Positioned3d}, use
 * {@code coords().to2d()}.
 */
public interface Positioned3d {
    /**
     * The position of the object in voxel world.
     *
     * @return the voxel coordinate.
     */
    WorldCoords3d coords();
}
