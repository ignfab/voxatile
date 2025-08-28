package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ignfab.minalac.generator.utils.world3d.Bounded3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;

/**
 * A succession of line segments connected to each other forming a string.
 */
public class LineString3d implements Bounded3d, Shape3d {

    /**
     * An empty {@code LineString3d} instance.
     */
    static final LineString3d EMPTY = new LineString3d(Collections.emptyList());

    /**
     * List of {@link Line2d} constituting the line string.
     */
    protected final List<Line3d> lines;
    private final WorldBBox3d bbox;

    /**
     * Creates a new {@code LineString3d} from a collection of lines.
     * This is a "raw" constructor not checking lines are correctly connected.
     *
     * @param lines the lines in this polyline
     */
    protected LineString3d(List<Line3d> lines) {
        this.lines = lines;
        bbox = WorldBBox3d.surrounding(lines);
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
    public Line3d get(int index) {
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
    public static LineString3d fromPoints(List<WorldCoords3d> points) {
        List<Line3d> lines = new ArrayList<>();
        for (int i = 1; i < points.size(); i++) {
            WorldCoords3d p1 = points.get(i - 1);
            WorldCoords3d p2 = points.get(i);
            if (p1 != p2)
                lines.add(new Line3d(points.get(i - 1), points.get(i)));
        }
        if (lines.size() == 0)
            return EMPTY;
        if (lines.size() == 1)
            return new LineString3d(Collections.singletonList(lines.get(0)));

        return new LineString3d(lines);
    }

    /**
     * Creates a new line string connecting all the given points in the given order.
     * Successive duplicate points will be removed.
     *
     * @param points the points of the line string.
     * @return a new line string.
     */
    public static LineString3d fromPoints(WorldCoords3d... points) {
        return fromPoints(Arrays.asList(points));
    }

    @Override
    public WorldBBox3d bbox() {
        return bbox;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{lines=%s}".formatted(String.join(", ", lines.stream().map(Line3d::toString).toList()));
    }

    // Shape3d implementation

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
    public Iterable<Line3d> lines() {
        return lines;
    }

    @Override
    public Iterable<LineString3d> lineStrings() {
        return Collections.singleton(this);
    }
}
