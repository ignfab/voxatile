package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A composite 2 dimensional shape made of other shapes.
 */
public class MultiShape2d implements Shape2d {

    private final List<Shape2d> shapes;

    /**
     * Creates a new shape collection.
     *
     * @param shapes shapes to put in collection
     */
    public MultiShape2d(Shape2d... shapes) {
        this.shapes = new ArrayList<>(Arrays.asList(shapes));
    }

    /**
     * Adds a shape to collection.
     *
     * @param shape shape to add
     */
    public void addShape(Shape2d shape) {
        shapes.add(shape);
    }

    @Override
    public Iterable<Point2d> points() {
        return Iterables.flatMap(shapes, Shape2d::points);
    }

    @Override
    public Iterable<LineString2d> lineStrings() {
        return Iterables.flatMap(shapes, Shape2d::lineStrings);
    }

    @Override
    public Iterable<Polygon2d> polygons() {
        return Iterables.flatMap(shapes, Shape2d::polygons);
    }

    @Override
    public WorldBBox2d bbox() {
        return WorldBBox2d.surrounding(shapes);
    }
}
