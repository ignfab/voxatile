package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A {@link LineString2d} that forms a ring. Last segment end is connected to first one start.
 */
public class LinearRing2d extends LineString2d {

    /**
     * An empty {@code LinearRing2d} instance.
     */
    static final LinearRing2d EMPTY = new LinearRing2d(Collections.emptyList());

    /**
     * Creates a new {@code LinearRing2d} from a collection of lines.
     * This is a "raw" constructor not checking lines are correctly connected.
     *
     * @param lines List of {@link Line2d}
     */
    protected LinearRing2d(List<Line2d> lines) {
        super(lines);
    }

    /**
     * Creates a new linear ring connecting all the given points in the given order.
     * Successive duplicate points will be removed.
     *
     * @param points the points of the linear ring (no need to repeat first point as end point).
     * @return a new linear ring.
     */
    public static LinearRing2d fromPoints(List<WorldCoords2d> points) {
        List<Line2d> lines = new ArrayList<>();
        if (points.size() > 0) {
            WorldCoords2d p1 = points.get(0);
            WorldCoords2d p2;
            for (int i = 1; i < points.size(); i++) {
                p2 = points.get(i);
                if (p1 != p2)
                    lines.add(new Line2d(p1, p2));
                p1 = p2;
            }
            p2 = points.get(0);
            if (p1 != p2)
                lines.add(new Line2d(p1, p2));
        }

        if (lines.size() == 0)
            return EMPTY;

        return new LinearRing2d(lines);
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

    @Override
    public Line2d get(int index) {
        return lines.get(Math.floorMod(index, size()));
    }

    // A linear ring could be considered as a polygon
    @Override
    public Iterable<Polygon2d> polygons() {
        return List.of(new Polygon2d(this, Collections.emptyList()));
    }
}
