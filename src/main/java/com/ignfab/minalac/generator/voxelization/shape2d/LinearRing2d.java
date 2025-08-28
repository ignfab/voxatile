package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.Arrays;
import java.util.List;

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
}
