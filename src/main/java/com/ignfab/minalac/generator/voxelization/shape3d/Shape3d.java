package com.ignfab.minalac.generator.voxelization.shape3d;

import com.ignfab.minalac.generator.utils.world3d.Bounded3d;

/**
 * Interface for voxel shapes in 3 dimensions.
 */
public interface Shape3d extends Shape3dConvertible, Bounded3d {
    /**
     * Returns an iterable over points in the shape.
     * <p>
     * All unique points must be returned. This iterator might return the same point multiple time
     * but it's better if shapes return only unique points.
     * <p>
     * Linestrings and polygons could be constitued of nothing (no lines, no rings) but a single
     * point if they are smaller than a voxel.
     *
     * @return iterable over all points.
     */
    Iterable<Point3d> points();

    /**
     * {@return iterable over all line strings in the shape}
     */
    Iterable<LineString3d> lineStrings();

    /**
     * {@return iterable over all polygons in the shape}
     */
    Iterable<Polygon3d> polygons();

    @Override
    default Shape3d toShape3d() {
        return this;
    }
}
