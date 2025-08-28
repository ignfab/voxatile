package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.Collections;

/**
 * Interface for voxel shapes in 3 dimensions.
 */
public interface Shape3d extends Shape3dConvertible {
    /**
     * Returns an iterable over all lines in this shape.
     *
     * @return iterable over all lines.
     */
    default Iterable<Line3d> lines() {
        return Collections::emptyIterator;
    }

    /**
     * Returns an iterable over all line strings in this shape.
     *
     * @return iterable over all line strings.
     */
    default Iterable<LineString3d> lineStrings() {
        return Collections::emptyIterator;
    }

    @Override
    default Shape3d toShape3d() {
        return this;
    }
}
