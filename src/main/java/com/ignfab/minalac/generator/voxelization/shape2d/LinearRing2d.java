package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A {@link LineString2d} that forms a ring. Last segment end is connected to first one start.
 */
public class LinearRing2d extends LineString2d {

    /**
     * An empty {@code LinearRing2d} instance.
     */
    public static final LinearRing2d EMPTY = new LinearRing2d();

    /**
     * Creates a new linear ring connecting all the given points in the given order.
     * Successive duplicate points will be removed.
     *
     * @param points the points of the linear ring (no need to repeat first point as end point).
     * @return a new linear ring.
     */
    public static LinearRing2d fromPoints(List<WorldCoords2d> points) {
        // Not a linear ring if less that 2 points
        if (points.size() < 2)
            return EMPTY;

        return new LinearRing2d(points);
    }

    /**
     * Creates a new linear ring connecting all the given points in the given order.
     * Successive duplicate points will be removed.
     *
     * @param points the points of the linear ring (no need to repeat first point as end point).
     * @return a new linear ring.
     */
    public static LinearRing2d fromPoints(WorldCoords2d... points) {
        return fromPoints(Arrays.asList(points));
    }

    protected LinearRing2d(List<WorldCoords2d> points) {
        super(points);
        if (!segments.isEmpty()) {
            Segment2d first = segments.get(0);
            Segment2d last = segments.get(segments.size() - 1);
            if (!first.start().equals(last.end()))
                segments.add(new Segment2d(last.end(), first.start()));
        }
    }

    protected LinearRing2d() {}

    @Override
    public Segment2d get(int index) {
        if (segments.isEmpty())
            return null;

        return segments.get(Math.floorMod(index, segments.size()));
    }

    /**
     * Determines if the ring is oriented clockwise.
     *
     * @return true if the ring is oriented clockwise, false otherwise
     */
    public boolean isClockwise() {
        // The code below follows this algorithm:
        // https://en.wikipedia.org/wiki/Shoelace_formula#Shoelace_formula
        //
        // The result of the shoelace formula provides two information:
        // - Its signs gives the ring orientation;
        // - Its absolute value is twice the ring area (should be used if we need to compute that area)
        int sum = 0;
        for (Segment2d segment : segments) {
            WorldCoords2d p1 = segment.start();
            WorldCoords2d p2 = segment.end();

            sum += p1.x() * p2.y() - p2.x() * p1.y();
        }
        // Result is positive for positively oriented (counter-clockwise)
        // polygons and negative for negatively oriented (clockwise) ones
        return sum < 0;
    }

    /**
     * {@return a new {@code LinearRing2d} with the order of points reversed}
     */
    public LinearRing2d invert() {
        List<WorldCoords2d> points = new ArrayList<>(size());
        ListIterator<Segment2d> iterator = segments.listIterator(size());
        while (iterator.hasPrevious())
            points.add(iterator.previous().start());
        return fromPoints(points);
    }

    /**
     * {@return a LinearRing2d oriented clockwise}
     */
    public LinearRing2d toClockwise() {
        return isClockwise() ? this : invert();
    }

    /**
     * {@return a LinearRing2d oriented counterclockwise}
     */
    public LinearRing2d toCounterClockwise() {
        return isClockwise() ? invert() : this;
    }
}
