package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A succession of line segments connected to each other forming a string.
 */
public class LineString2d implements Shape2d {

    /**
     * An empty {@code LineString2d} instance.
     */
    public static final LineString2d EMPTY = new LineString2d();

    private final WorldBBox2d bbox;

    private final Set<Point2d> points;

    /**
     * List of {@link Segment2d} constituting the line string.
     */
    protected final List<Segment2d> segments;

    /**
     * Creates a new line string connecting all the given points in the given order.
     * Successive duplicate points will be removed.
     *
     * @param points the points of the line string.
     * @return a new line string.
     */
    public static LineString2d fromPoints(List<WorldCoords2d> points) {
        // Not a line string if less that 2 points
        if (points.size() < 2)
            return EMPTY;

        return new LineString2d(points);
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

    protected LineString2d(List<WorldCoords2d> points) {
        // TODO: A été changer voir si on fait plus propre
        this.points = new LinkedHashSet<>();
        segments = new ArrayList<>();

        WorldCoords2d p1 = points.get(0);
        this.points.add(new Point2d(p1));

        for (int i = 1; i < points.size(); i++) {
            WorldCoords2d p2 = points.get(i);
            if (!p1.equals(p2)) {
                this.points.add(new Point2d(p2));
                segments.add(new Segment2d(p1, p2));
            }
            p1 = p2;
        }

        bbox = new WorldBBox2d(points.toArray(WorldCoords2d[]::new));
    }

    protected LineString2d() {
        segments = Collections.emptyList();
        points = Collections.emptySet();
        bbox = WorldBBox2d.EMPTY;
    }

    /**
     * Retrieves a line segment from its index.
     *
     * @param index Index of segment to get
     * @return segment at given index or null if none
     */
    public Segment2d get(int index) {
        if (index < 0 || index >= segments.size())
            return null;
        return segments.get(index);
    }

    /**
     * {@return the number of segments}
     */
    public int size() {
        return segments.size();
    }

    /**
     * Returns an iterable over all segments in the {@code LineString2d}.
     * <p>
     * Segments are given in such order that end of previous correspond to start of next.
     *
     * @return an iterable over segments
     */
    public Iterable<Segment2d> segments() {
        return segments;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This will return only unique points.
     */
    @Override
    public Iterable<Point2d> points() {
        return points;
    }

    @Override
    public Iterable<LineString2d> lineStrings() {
        return Collections.singleton(this);
    }

    @Override
    public Iterable<Polygon2d> polygons() {
        return Collections::emptyIterator;
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public String toString() {
        if (segments.isEmpty())
            if (points.isEmpty())
                return getClass().getSimpleName() + "[empty]";
            else
                return getClass().getSimpleName() + "[single point at %s]".formatted(points.iterator().next());
        else
            return getClass().getSimpleName() + "[segments=%s]".formatted(segments);
    }

    @Override
    public int hashCode() {
        return 31 * segments.hashCode() + points.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;
        LineString2d that = (LineString2d) o;

        return points.equals(that.points) && segments.equals(that.segments);
    }
}
