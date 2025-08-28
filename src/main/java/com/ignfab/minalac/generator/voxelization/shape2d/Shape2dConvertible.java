package com.ignfab.minalac.generator.voxelization.shape2d;

/**
 * Something that can be converted to a {@link Shape2d}.
 */
public interface Shape2dConvertible {
    /**
     * Converts object to {@link Shape2d}.
     *
     * @return {@link Shape2d} resulting from conversion.
     */
    Shape2d toShape2d();
}
