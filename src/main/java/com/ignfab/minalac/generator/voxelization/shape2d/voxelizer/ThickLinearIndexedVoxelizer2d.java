package com.ignfab.minalac.generator.voxelization.shape2d.voxelizer;

import java.util.Collections;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2dConvertible;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.IndexedPosition2d;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.ThickLineString2dIndexedIterator;

/**
 * A voxelizer for linear shapes, with thickness and indexed results.
 * <p>
 * Indexed results mean shapes segments relative coordinates are returned along with voxel coordinates.
 */
public class ThickLinearIndexedVoxelizer2d implements Shape2dVoxelizer {

    private final double thickness;

    /**
     * Creates a new {@code ThickLinearIndexedVoxelizer2d}.
     *
     * @param thickness Thickness, in voxels, of drawn lines.
     */
    public ThickLinearIndexedVoxelizer2d(double thickness) {
        this.thickness = thickness;
    }

    /**
     * Voxelizes a line string (or linear ring).
     * <p>
     * Segments are drawn with ends cut and lengthen to connect to each other well.
     * All segments share the same x (long) index that goes from 0 to line string length.
     *
     * @param lineString Line string to voxelize
     * @return an iterable over voxelized positions
     */
    public Iterable<IndexedPosition2d> voxelizeShape2d(LineString2d lineString) {
        return () -> new ThickLineString2dIndexedIterator(lineString, thickness);
    }

    @Override
        public Iterable<IndexedPosition2d> voxelizeShape2d(Shape2d shape) {
        return Iterables.flatMap(shape.lineStrings(), this::voxelizeShape2d);
    }

    // Needed to precise return type
    @Override
    public Iterable<IndexedPosition2d> voxelize(Model model) {
        if (model instanceof Shape2dConvertible convertible)
            return voxelizeShape2d(convertible.toShape2d());
        else
            return Collections.emptyList();
    }
}
