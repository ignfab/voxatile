package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;

/**
 * A {@link LineString2d} that forms a ring. Last segment end is connected to first one start.
 */
public class LinearRing3d extends LineString3d {

    /**
     * An empty {@code LinearRing3d} instance.
     */
    static final LinearRing3d EMPTY = new LinearRing3d(Collections.emptyList());

    /**
     * Creates a new {@code LinearRing3d} from a collection of lines.
     * This is a "raw" constructor not checking lines are correctly connected.
     *
     * @param lines List of {@link Line3d}
     */
    protected LinearRing3d(List<Line3d> lines) {
        super(lines);
    }

    /**
     * Creates a new linear ring connecting all the given points in the given order.
     * Successive duplicate points will be removed.
     *
     * @param points the points of the linear ring (no need to repeat first point as end point).
     * @return a new linear ring.
     */
    public static LinearRing3d fromPoints(List<WorldCoords3d> points) {
        List<Line3d> lines = new ArrayList<>();
        if (points.size() > 0) {
            WorldCoords3d p1 = points.get(0);
            WorldCoords3d p2;
            for (int i = 1; i < points.size(); i++) {
                p2 = points.get(i);
                if (p1 != p2)
                    lines.add(new Line3d(p1, p2));
                p1 = p2;
            }
            p2 = points.get(0);
            if (p1 != p2)
                lines.add(new Line3d(p1, p2));
        }

        if (lines.size() == 0)
            return EMPTY;

        return new LinearRing3d(lines);
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

    @Override
    public Line3d get(int index) {
        return lines.get(Math.floorMod(index, size()));
    }

    // A linear ring could be considered as a polygon
    @Override
    public Iterable<Polygon3d> polygons() {
        return List.of(new Polygon3d(this, Collections.emptyList()));
    }
}
