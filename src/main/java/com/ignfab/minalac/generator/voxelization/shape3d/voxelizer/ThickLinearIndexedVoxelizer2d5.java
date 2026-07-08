package com.ignfab.minalac.generator.voxelization.shape3d.voxelizer;

import java.util.Collections;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.voxelization.shape3d.LineString3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Shape3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Shape3dConvertible;
import com.ignfab.minalac.generator.voxelization.shape3d.iterator.IndexedPosition3d;
import com.ignfab.minalac.generator.voxelization.shape3d.iterator.ThickLineString2d5IndexedIterator;

/**
 * A voxelizer for linear shapes, with thickness and indexed results.
 * <p>
 * Indexed results mean shapes segments relative coordinates are returned along with voxel coordinates.
 * <p>
 * This is not a full 3-dimensional voxelizer. It is rather a 2.5d one. Only 2d index is returned.
 * Voxelization is made by corresponding 2d iterator and a z-component is added.
 */
public class ThickLinearIndexedVoxelizer2d5 implements Shape3dVoxelizer {
        private final double thickness;

    /**
     * Creates a new {@code ThickLinearIndexedVoxelizer3d}.
     *
     * @param thickness Horizontal thickness, in voxels, of drawn lines
     */
    public ThickLinearIndexedVoxelizer2d5(double thickness) {
        this.thickness = thickness;
    }

    /**
     * Voxelizes a line string (or linear ring).
     * <p>
     * Segments are drawn with ends cut and lengthen to connect to each other well.
     * All segments share the same x (long) index that goes from 0 to line string length.
     *
     * @param lineString Line string to voxelize
     * @return an iterable over voxelized positions and indexes
     */
    public Iterable<IndexedPosition3d> voxelizeShape3d(LineString3d lineString) {
        return () -> new ThickLineString2d5IndexedIterator(lineString, thickness);
    }

    @Override
    public Iterable<IndexedPosition3d> voxelizeShape3d(Shape3d shape) {
        return Iterables.flatMap(shape.lineStrings(), this::voxelizeShape3d);
    }

    // Needed to precise return type
    @Override
    public Iterable<IndexedPosition3d> voxelize(Model model) {
        if (model instanceof Shape3dConvertible convertible)
            return voxelizeShape3d(convertible.toShape3d());
        else
            return Collections.emptyList();
    }
}

