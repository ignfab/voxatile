package com.ignfab.minalac.generator.voxelization.shape3d;

import com.ignfab.minalac.generator.utils.Pair;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world3d.Bounded3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape3d.iterator.Line3dIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents a 3d polyline in the voxel world.
 * It consists of a list of lines, not necessary joined and not necessary closed.
 */
public class Polyline3d implements Bounded3d, Shape3d {
    private final List<Line3d> lines;
    private final WorldBBox3d bbox;

    /**
     * Creates a new polyline from a collection of lines.
     *
     * @param lines the lines in this polyline
     */
    public Polyline3d(List<Line3d> lines) {
        this.lines = lines;
        bbox = WorldBBox3d.surrounding(lines);
    }

    /**
     * Returns a list of lines constituting the polyline.
     *
     * @return the list of lines
     */
    public List<Line3d> lines() {
        return lines;
    }

    /**
     * Creates a new polyline connecting all points in the given list.
     *
     * @param points the points of the polyline.
     * @return a new polyline.
     */
    public static Polyline3d fromPoints(List<WorldCoords3d> points) {
        if (points.size() < 2)
            return new Polyline3d(Collections.emptyList());
        if (points.size() == 2)
            return new Polyline3d(Collections.singletonList(new Line3d(points.get(0), points.get(1))));
        List<Line3d> lines = new ArrayList<>();
        for (int i = 1; i < points.size(); i++)
            lines.add(new Line3d(points.get(i - 1), points.get(i)));
        return new Polyline3d(lines);
    }

    /**
     * Creates a new polyline connecting all the given points.
     *
     * @param points the points of the polyline.
     * @return a new polyline.
     */
    public static Polyline3d fromPoints(WorldCoords3d... points) {
        return fromPoints(Arrays.asList(points));
    }

    @Override
    public WorldBBox3d bbox() {
        return bbox;
    }

    @Override
    public Iterable<LinearVoxel3d> borderVoxels() {
        return Iterables.unwrap(
            Iterables.remap(
                () -> new LinesIterator(lines),
                (Pair<Line3d, Line3d> p) -> () -> new Line3dIterator(p.first(), p.second())
            )
        );
    }

    /**
     * An iterator over lines that returns current and next line and closes the loop if in a ring.
     */
    public class LinesIterator implements Iterator<Pair<Line3d, Line3d>> {

        private Iterator<Line3d> iterator;
        private Line3d next;
        private Line3d first;

        /**
         * Creates a new LinesIterator from an {@code Iterable<Line3d>}.
         *
         * @param iterable iterable to create iterator from
         */
        public LinesIterator(Iterable<Line3d> iterable) {
            iterator = iterable.iterator();
            if (iterator.hasNext()) {
                first = iterator.next();
                next = first;
            } else {
                next = null;
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Pair<Line3d, Line3d> next() {
            if (next == null)
                throw new NoSuchElementException();

            Line3d last = next;
            next = (iterator.hasNext()) ? iterator.next() : null;

            // Close the loop if we are in a de facto linear ring
            if (next == null && last.end() == first.start())
                return new Pair<>(last, first);

            return new Pair<>(last, next);
        }
    }
}
