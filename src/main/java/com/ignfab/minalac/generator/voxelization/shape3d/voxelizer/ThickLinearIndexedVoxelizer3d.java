package com.ignfab.minalac.generator.voxelization.shape3d.voxelizer;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.voxelization.shape3d.Line3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineString3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Shape3dConvertible;
import com.ignfab.minalac.generator.voxelization.shape3d.iterator.Indexed2dPosition3d;
import com.ignfab.minalac.generator.voxelization.shape3d.iterator.ThickLine3dIterator;
import com.ignfab.minalac.generator.voxelization.shape3d.iterator.ThickLineString3dIndexedIterator;

/**
 * A voxelizer for linear shapes, with thickness and indexed results.
 * <p>
 * Indexed results mean line relative coordinates are returned along with voxel coordinates.
 * <p>
 * This is not a full 3-dimensional voxelizer. It is rather a 2.5d one. Only 2d index is returned.
 * Voxelization is made by corresponding 2d iterator and a z-component is added.
 */
public class ThickLinearIndexedVoxelizer3d {
        private final double thickness;

    /**
     * Creates a new {@code ThickLinearIndexedVoxelizer3d}.
     *
     * @param thickness Horizontal thickness, in voxels, of drawn lines
     */
    public ThickLinearIndexedVoxelizer3d(double thickness) {
        this.thickness = thickness;
    }

    /**
     * Voxelizes a single line. Line is drawn with right angle ends.
     *
     * @param line Line to voxelize
     * @return an iterable over voxelized positions and indexes
     */
    public Iterable<Indexed2dPosition3d> voxelize(Line3d line) {
        return () -> Iterators.remap(new ThickLine3dIterator(line, thickness), (position) -> new Indexed2dPosition3d(position.coords(), line.to2d().convertLineRelative(position.coords().to2d())));
    }

    /**
     * Voxelizes a line string (including rings). Lines are drawn with ends cut and lengthen to connect to each other well.
     * A line string shares the same x (long) index that goes from 0 to line string length.
     *
     * @param lineString Line string to voxelize
     * @return an iterable over voxelized positions and indexes
     */
    public Iterable<Indexed2dPosition3d> voxelize(LineString3d lineString) {
        return () -> new ThickLineString3dIndexedIterator(lineString, thickness);
    }

    /**
     * Voxelizes any other shapes, using line or linestring voxelization.
     *
     * @param convertible Something convertible to a {@code Shape3d}
     * @return an iterable over voxelized positions and indexes
     */
    public Iterable<Indexed2dPosition3d> voxelize(Shape3dConvertible convertible) {
        return Iterables.unwrap(Iterables.remap(convertible.toShape3d().lineStrings(), this::voxelize));
    }
}
