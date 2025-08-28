package com.ignfab.minalac.generator.voxelization.shape2d.voxelizer;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2dConvertible;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.IndexedPosition2d;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.ThickLine2dIterator;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.ThickLineSting2dIndexedIterator;

/**
 * A voxelizer for linear shapes, with thickness and indexed results.
 * <p>
 * Indexed results mean line relative coordinates are returned along with voxel coordinates.
 */
public class ThickLinearIndexedVoxelizer2d {

    private final double thickness;

    /**
     * Creates a new {@code ThickLinearIndexedVoxelizer2d}.
     *
     * @param thickness Thickness, in voxels, of drawn lines
     */
    public ThickLinearIndexedVoxelizer2d(double thickness) {
        this.thickness = thickness;
    }

    /**
     * Voxelizes a single line. Line is drawn with right angle ends.
     *
     * @param line Line to voxelize
     * @return an iterable over voxelized positions and indexes
     */
    public Iterable<IndexedPosition2d> voxelize(Line2d line) {
        return () -> Iterators.remap(new ThickLine2dIterator(line, thickness), (position) -> new IndexedPosition2d(position.coords(), line.convertLineRelative(position.coords())));
    }

    /**
     * Voxelizes a line string (including rings). Lines are drawn with ends cut and lengthen to connect to each other well.
     * A line string shares the same x (long) index that goes from 0 to line string length.
     *
     * @param lineString Line string to voxelize
     * @return an iterable over voxelized positions and indexes
     */
    public Iterable<IndexedPosition2d> voxelize(LineString2d lineString) {
        return () -> new ThickLineSting2dIndexedIterator(lineString, thickness);
    }

    /**
     * Voxelizes any other shapes, using line or linestring voxelization.
     *
     * @param convertible Something convertible to a {@code Shape2d}
     * @return an iterable over voxelized positions and indexes
     */
    public Iterable<IndexedPosition2d> voxelize(Shape2dConvertible convertible) {
        return Iterables.unwrap(Iterables.remap(convertible.toShape2d().lineStrings(), this::voxelize));
    }

}
