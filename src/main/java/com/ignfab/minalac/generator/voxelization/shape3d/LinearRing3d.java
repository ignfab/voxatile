package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.Arrays;
import java.util.List;

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
}
