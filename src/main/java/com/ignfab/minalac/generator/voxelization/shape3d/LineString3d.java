package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A succession of line segments connected to each other forming a string.
 */
public class LineString3d implements Shape3d {

    /**
     * An empty {@code LineString3d} instance.
     */
    public static final LineString3d EMPTY = new LineString3d();

    private final WorldBBox3d bbox;

    private final Set<Point3d> points;

    /**
     * List of {@link Segment3d} constituting the line string.
     */
    protected final List<Segment3d> segments;

    /**
     * Creates a new line string connecting all the given points in the given order.
     * Successive duplicate points will be removed.
     *
     * @param points the points of the line string.
     * @return a new line string.
     */
    public static LineString3d fromPoints(List<WorldCoords3d> points) {
        // Not a line string if less that 2 points
        if (points.size() < 2)
            return EMPTY;

        return new LineString3d(points);
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

    protected LineString3d(List<WorldCoords3d> points) {
        this.points = new HashSet<>();
        segments = new ArrayList<>();

        WorldCoords3d p1 = points.get(0);
        this.points.add(new Point3d(p1));

        for (int i = 1; i < points.size(); i++) {
            WorldCoords3d p2 = points.get(i);
            if (!p1.equals(p2)) {
                this.points.add(new Point3d(p2));
                segments.add(new Segment3d(p1, p2));
            }
            p1 = p2;
        }

        bbox = new WorldBBox3d(points.toArray(WorldCoords3d[]::new));
    }

    protected LineString3d() {
        segments = Collections.emptyList();
        points = Collections.emptySet();
        bbox = WorldBBox3d.EMPTY;
    }

    /**
     * Retrieves a line segment from its index.
     *
     * @param index Index of segment to get
     *
     * @return segment at given index or null if none
     */
    public Segment3d get(int index) {
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
     * Returns an iterable over all segments in the {@code LineString3d}.
     * <p>
     * Segments are given in such order that end of previous correspond to start of next.
     *
     * @return an iterable over segments
     */
    public Iterable<Segment3d> segments() {
        return segments;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This will return only unique points.
     */
    @Override
    public Iterable<Point3d> points() {
        return points;
    }

    @Override
    public Iterable<LineString3d> lineStrings() {
        return Collections.singleton(this);
    }

    @Override
    public Iterable<Polygon3d> polygons() {
        return Collections::emptyIterator;
    }

    @Override
    public WorldBBox3d bbox() {
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
        int hash = 0;
        for (Segment3d line : segments)
            hash = 31 * hash + line.hashCode();
        for (Point3d point : points)
            hash = 32 * hash + point.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineString3d that = (LineString3d) o;

        if (segments.size() != that.segments.size()) return false;
        if (points.size() != that.points.size()) return false;

        if (!segments.isEmpty())
            for (int index = 0; index < segments.size(); index++)
                if (!segments.get(index).equals(that.segments.get(index)))
                    return false;
        else
            if (!(points.containsAll(that.points) && that.points.containsAll(points)))
                return false;

        return true;
    }
}
