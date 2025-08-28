package com.ignfab.minalac.generator.voxelization.shape3d.voxelizer;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Line3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineString3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Shape3dConvertible;
import com.ignfab.minalac.generator.voxelization.shape3d.iterator.ThickLine3dIterator;
import com.ignfab.minalac.generator.voxelization.shape3d.iterator.ThickLineString3dIterator;

/**
 * A voxelizer for linear shapes, with thickness.
 * This is not a full 3-dimensional voxelizer. It is rather a 2.5d one.
 * Voxelization is made by corresponding 2d iterator and a z-component is added.
 */
public class ThickLinearVoxelizer3d {

    private final double thickness;

    /**
     * Creates a new {@code ThickLinearVoxelizer2d}.
     *
     * @param thickness Horizontal thickness, in voxels, of drawn lines
     */
    public ThickLinearVoxelizer3d(double thickness) {
        this.thickness = thickness;
    }

    /**
     * Voxelizes a single line.
     * Line is drawn with right angle ends.
     *
     * @param line Line to voxelize
     * @return an iterable over voxelized positions
     */
    public Iterable<Positioned3d> voxelize(Line3d line) {
        return () -> new ThickLine3dIterator(line, thickness);
    }

    /**
     * Voxelizes a line string (including rings).
     * Lines are drawn with ends cut and lengthen to connect to each other well.
     *
     * @param lineString Line string to voxelize
     * @return an iterable over voxelized positions
     */
    public Iterable<Positioned3d> voxelize(LineString3d lineString) {
        return () -> new ThickLineString3dIterator(lineString, thickness);
    }

    /**
     * Voxelizes any other shapes, using line or linestring voxelization.
     *
     * @param convertible Something convertible to a {@code Shape3d}
     * @return an iterable over voxelized positions
     */
    public Iterable<Positioned3d> voxelize(Shape3dConvertible convertible) {
        return Iterables.unwrap(Iterables.remap(convertible.toShape3d().lineStrings(), this::voxelize));
    }
}
