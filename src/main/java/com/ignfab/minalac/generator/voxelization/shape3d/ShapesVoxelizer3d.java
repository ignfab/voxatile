package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer3d;

/**
 * A voxelizer based on 3d shapes.
 */
public class ShapesVoxelizer3d implements Voxelizer3d {
    private final WorldBBox3d bbox;
    private final List<Shape3d> shapes;

    /**
     * Creates a new voxelizer with the given limits.
     *
     * @param bbox the limits of the returned voxels.
     */
    public ShapesVoxelizer3d(WorldBBox3d bbox) {
        this.bbox = bbox;
        shapes = new ArrayList<>();
    }

    // Constructor for empty voxelizer
    private ShapesVoxelizer3d() {
        bbox = null;
        shapes = null;
    }

    /**
     * Adds a shape to the stored shapes.
     *
     * @param shape the shape to add.
     */
    public void addShape(Shape3d shape) {
        shapes.add(shape);
    }

    /**
     * Returns an iterable over voxels on the border of shapes.
     * Additional information is available in {@code LineVoxel3d} about lines
     * and position on them.
     *
     * @return an iterable over border voxels
     */
    public Iterable<LineVoxel3d> borders() {
        return () -> bbox.crop(Iterables.unwrap(Iterables.remap(shapes, Shape3d::borderVoxels)));
    }

    /**
     * Returns an iterable over voxels inside shapes (excluding borders).
     *
     * @return an iterable over inside voxels
     */
    public Iterable<Positioned3d> inside() {
        return () -> bbox.crop(Iterables.unwrap(Iterables.remap(shapes, Shape3d::insideVoxels)));
    }

    @Override
    public Iterator<Positioned3d> iterator() {
        return bbox.crop(Iterables.unwrap(Iterables.remap(shapes, Shape3d::allVoxels)));
    }

    /**
     * An empty {@code ShapeVoxelizer3d} for convenience.
     */
    public static final ShapesVoxelizer3d EMPTY = new Empty();

    private static final class Empty extends ShapesVoxelizer3d {
        @Override
        public void addShape(Shape3d shape) {}

        @Override
        public Iterator<Positioned3d> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public Iterable<LineVoxel3d> borders() {
            return Collections::emptyIterator;
        }

        @Override
        public Iterable<Positioned3d> inside() {
            return Collections::emptyIterator;
        }
    }
}
