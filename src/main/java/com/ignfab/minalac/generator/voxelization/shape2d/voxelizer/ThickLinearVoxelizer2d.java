package com.ignfab.minalac.generator.voxelization.shape2d.voxelizer;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2dConvertible;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.ThickLine2dIterator;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.ThickLineSting2dIterator;

/**
 * A voxelizer for linear shapes, with thickness.
 */
public class ThickLinearVoxelizer2d {

    private final double thickness;

    /**
     * Creates a new {@code ThickLinearVoxelizer2d}.
     *
     * @param thickness Thickness, in voxels, of drawn lines.
     */
    public ThickLinearVoxelizer2d(double thickness) {
        this.thickness = thickness;
    }

    /**
     * Voxelizes a single line.
     * Line is drawn with right angle ends.
     *
     * @param line Line to voxelize
     * @return an iterable over voxelized positions
     */
    public Iterable<Positioned2d> voxelize(Line2d line) {
        return () -> new ThickLine2dIterator(line, thickness);
    }

    /**
     * Voxelizes a line string (including rings).
     * Lines are drawn with ends cut and lengthen to connect to each other well.
     *
     * @param lineString Line string to voxelize
     * @return an iterable over voxelized positions
     */
    public Iterable<Positioned2d> voxelize(LineString2d lineString) {
        return () -> new ThickLineSting2dIterator(lineString, thickness);
    }

    /**
     * Voxelizes any other shapes, using line or linestring voxelization.
     *
     * @param convertible Something convertible to a {@code Shape2d}.
     * @return an iterable over voxelized positions
     */
    public Iterable<Positioned2d> voxelize(Shape2dConvertible convertible) {
        return Iterables.unwrap(Iterables.remap(convertible.toShape2d().lineStrings(), this::voxelize));
    }

}
