package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A {@link LineString3d} that forms a ring. Last segment end is connected to first one start.
 */
public class LinearRing3d extends LineString3d {

    /**
     * An empty {@code LinearRing3d} instance.
     */
    static final LinearRing3d EMPTY = new LinearRing3d();

    /**
     * Creates a new linear ring connecting all the given points in the given order.
     * Successive duplicate points will be removed.
     *
     * @param points the points of the linear ring (no need to repeat first point as end point).
     * @return a new linear ring.
     */
    public static LinearRing3d fromPoints(List<WorldCoords3d> points) {
        // Not a linear ring if less that 2 points
        if (points.size() < 2)
            return EMPTY;

        return new LinearRing3d(points);
    }

    /**
     * Creates a new linear ring connecting all the given points in the given order.
     * Successive duplicate points will be removed.
     *
     * @param points the points of the linear ring (no need to repeat first point as end point).
     * @return a new linear ring.
     */
    public static LinearRing3d fromPoints(WorldCoords3d... points) {
        return fromPoints(Arrays.asList(points));
    }

    protected LinearRing3d(List<WorldCoords3d> points) {
        super(points);
        if (!segments.isEmpty()) {
            Segment3d first = segments.get(0);
            Segment3d last = segments.get(segments.size() - 1);
            if (!first.start().equals(last.end()))
                segments.add(new Segment3d(last.end(), first.start()));
        }
    }

    protected LinearRing3d() {}

    @Override
    public Segment3d get(int index) {
        if (segments.isEmpty())
            return null;

        return segments.get(Math.floorMod(index, segments.size()));
    }

    /**
     * Determines if the ring is oriented clockwise on XY plane (ignoring Z).
     *
     * @return true if the ring is oriented clockwise, false otherwise
     */
    public boolean isClockwiseXY() {
        // The code below follows this algorithm:
        // https://en.wikipedia.org/wiki/Shoelace_formula#Shoelace_formula
        //
        // The result of the shoelace formula provides two information:
        // - Its signs gives the ring orientation;
        // - Its absolute value is twice the ring area (should be used if we need to compute that area)
        int sum = 0;
        for (Segment3d segment : segments) {
            WorldCoords3d p1 = segment.start();
            WorldCoords3d p2 = segment.end();

            sum += p1.x() * p2.y() - p2.x() * p1.y();
        }
        // Result is positive for positively oriented (counter-clockwise)
        // polygons and negative for negatively oriented (clockwise) ones
        return sum < 0;
    }

    /**
     * {@return a new {@code LinearRing3d} with the order of points reversed}
     */
    public LinearRing3d invert() {
        List<WorldCoords3d> points = new ArrayList<>(size());
        ListIterator<Segment3d> iterator = segments.listIterator(size());
        while (iterator.hasPrevious())
            points.add(iterator.previous().start());
        return fromPoints(points);
    }

    /**
     * {@return a LinearRing3d oriented clockwise on XY plane}
     */
    public LinearRing3d toClockwiseXY() {
        return isClockwiseXY() ? this : invert();
    }

    /**
     * {@return a LinearRing3d oriented counterclockwise on XY plane}
     */
    public LinearRing3d toCounterClockwiseXY() {
        return isClockwiseXY() ? invert() : this;
    }
}
