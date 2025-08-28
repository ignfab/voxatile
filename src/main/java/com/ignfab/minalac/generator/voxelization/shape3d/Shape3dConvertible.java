package com.ignfab.minalac.generator.voxelization.shape3d;

/**
 * Something that can be converted to a {@link Shape3d}.
 */
public interface Shape3dConvertible {
    /**
     * Converts object to {@link Shape3d}.
     *
     * @return {@link Shape3d} resulting from conversion.
     */
    Shape3d toShape3d();
}
