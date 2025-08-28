package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * A composite 3 dimensional shape made of other shapes.
 */
public class MultiShape3d implements Shape3d {

    private final List<Shape3d> shapes;

    /**
     * Creates a new shape collection.
     *
     * @param shapes shapes to put in collection
     */
    public MultiShape3d(Shape3d... shapes) {
        this.shapes = new ArrayList<>(Arrays.asList(shapes));
    }

    /**
     * Adds a shape to collection.
     *
     * @param shape shape to add
     */
    public void addShape(Shape3d shape) {
        shapes.add(shape);
    }

    @Override
    public Iterable<Point3d> points() {
        return Iterables.unwrap(Iterables.remap(shapes, Shape3d::points));
    }

    @Override
    public Iterable<LineString3d> lineStrings() {
        return Iterables.unwrap(Iterables.remap(shapes, Shape3d::lineStrings));
    }

    @Override
    public Iterable<Polygon3d> polygons() {
        return Iterables.unwrap(Iterables.remap(shapes, Shape3d::polygons));
    }

    @Override
    public WorldBBox3d bbox() {
        return WorldBBox3d.surrounding(shapes);
    }
}

