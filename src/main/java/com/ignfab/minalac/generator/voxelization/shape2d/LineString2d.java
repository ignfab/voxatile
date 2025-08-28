package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ignfab.minalac.generator.utils.world2d.Bounded2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A succession of line segments connected to each other forming a string.
 */
public class LineString2d implements Bounded2d, Shape2d {

    /**
     * An empty {@code LineString2d} instance.
     */
    static final LineString2d EMPTY = new LineString2d(Collections.emptyList());

    /**
     * List of {@link Line2d} constituting the line string.
     */
    protected final List<Line2d> lines;
    private final WorldBBox2d bbox;

    /**
     * Creates a new {@code LineString2d} from a collection of lines.
     * This is a "raw" constructor not checking lines are correctly connected.
     *
     * @param lines the lines in this polyline
     */
    protected LineString2d(List<Line2d> lines) {
        this.lines = lines;
        bbox = WorldBBox2d.surrounding(lines);
    }

    /**
     * Number of segments in this {@link LineString2d}.
     *
     * @return the number of segments
     */
    public int size() {
        return lines.size();
    }

    /**
     * Retrieves a line segment from its index.
     *
     * @param index Index of segment to get
     *
     * @return segment at given index or null if none
     */
    public Line2d get(int index) {
        if (index < 0 || index >= lines.size())
            return null;
        return lines.get(index);
    }

    /**
     * Creates a new line string connecting all the given points in the given order.
     * Successive duplicate points will be removed.
     *
     * @param points the points of the line string.
     * @return a new line string.
     */
    public static LineString2d fromPoints(List<WorldCoords2d> points) {
        List<Line2d> lines = new ArrayList<>();
        for (int i = 1; i < points.size(); i++) {
            WorldCoords2d p1 = points.get(i - 1);
            WorldCoords2d p2 = points.get(i);
            if (p1 != p2)
                lines.add(new Line2d(points.get(i - 1), points.get(i)));
        }
        if (lines.size() == 0)
            return EMPTY;
        if (lines.size() == 1)
            return new LineString2d(Collections.singletonList(lines.get(0)));

        return new LineString2d(lines);
    }

    /**
     * Creates a new line string connecting all the given points in the given order.
     * Successive duplicate points will be removed.
     *
     * @param points the points of the line string.
     * @return a new line string.
     */
    public static LineString2d fromPoints(WorldCoords2d... points) {
        return fromPoints(Arrays.asList(points));
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{lines=%s}".formatted(String.join(", ", lines.stream().map(Line2d::toString).toList()));
    }

    // Shape2d implementation

    /**
     * TODO inherit docs
     * <p>
     *
     * Returns a list of segments constituting the line string.
     * Segments are given in such order that end of previous correspond to start of next.
     *
     * @return the list of lines
     */
    @Override
    public Iterable<Line2d> lines() {
        return lines;
    }

    @Override
    public Iterable<LineString2d> lineStrings() {
        return Collections.singleton(this);
    }
}
