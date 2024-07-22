package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.world2d.Bounded2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.LineVoxel2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a 2d polyline in the voxel world.
 * It consists of a list of lines, not necessary joined and not necessary closed.
 */
public class Polyline2d implements Bounded2d, Shape2d {
    private final List<Line2d> lines;
    private final WorldBBox2d bbox;

    /**
     * Creates a new polyline from a collection of lines.
     *
     * @param lines the lines in this polyline
     */
    public Polyline2d(List<Line2d> lines) {
        this.lines = lines;
        bbox = WorldBBox2d.surrounding(lines);
    }

    /**
     * Returns a list of lines constituting the polyline.
     *
     * @return the list of lines
     */
    public List<Line2d> lines() {
        return lines;
    }

    /**
     * Creates a new polyline connecting all points in the given list.
     * Resulting polyline will consist in one continuous path.
     *
     * @param points the points of the polyline.
     * @return a new polyline.
     */
     public static Polyline2d fromPoints(List<WorldCoords2d> points) {
        if (points.size() < 2)
            return new Polyline2d(Collections.emptyList());
        if (points.size() == 2)
            return new Polyline2d(Collections.singletonList(new Line2d(points.get(0), points.get(1))));
        List<Line2d> lines = new ArrayList<>();
        for (int i = 1; i < points.size(); i++)
            lines.add(new Line2d(points.get(i - 1), points.get(i)));
        return new Polyline2d(lines);
    }

    /**
     * Creates a new polyline connecting all the given points.
     * Resulting polyline will consist in one continuous path.
     *
     * @param points the points of the polyline.
     * @return a new polyline.
     */
    public static Polyline2d fromPoints(WorldCoords2d... points) {
        return fromPoints(Arrays.asList(points));
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public Iterable<LineVoxel2d> borderVoxels() {
        return () -> new MultiIterator<>(new RemapIterator<>(lines, Shape2d::borderVoxels));
    }

    @Override
    public Iterable<Voxel2d> insideVoxels() {
        return Collections::emptyIterator;
    }
}
