package fr.ign.voxatile.core.voxelization.shape2d;

import fr.ign.voxatile.core.utils.world2d.Bounded2d;

/**
 * Interface for voxel shapes in 2 dimensions.
 */
public interface Shape2d extends Shape2dConvertible, Bounded2d {
    /**
     * Returns an iterable over points in the shape.
     * <p>
     * All unique points must be returned. This iterator might return the same point multiple time
     * but it's better if shapes return only unique points.
     * <p>
     * Linestrings and polygons could be constituted of nothing (no lines, no rings) but a single
     * point if they are smaller than a voxel.
     *
     * @return iterable over all points.
     */
    Iterable<Point2d> points();

    /**
     * {@return iterable over all line strings in the shape}
     */
    Iterable<LineString2d> lineStrings();

    /**
     * {@return iterable over all polygons in the shape}
     */
    Iterable<Polygon2d> polygons();

    @Override
    default Shape2d toShape2d() {
        return this;
    }
}
