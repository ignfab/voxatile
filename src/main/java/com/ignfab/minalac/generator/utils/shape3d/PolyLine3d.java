package com.ignfab.minalac.generator.utils.shape3d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.LineVoxel3d;
import com.ignfab.minalac.generator.voxelization.Voxel3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a 3d polyline in the voxel world.
 * It consists of a list of lines where the end
 * of one must be the start of the next one.
 * There is no distinction between a closed and an opened polyline.
 *
 * @param lines the lines in this polyline.
 */
public record PolyLine3d(List<Line3d> lines) implements Shape3d {
    /**
     * Computes the bounding box of this polyline.
     * This is the smallest box containing all the lines.
     *
     * @return the bounding box of this polyline.
     */
    public WorldBBox3d bbox() {
        if (lines.isEmpty())
            return WorldBBox3d.EMPTY;
        Line3d line = lines.get(0);
        if (lines.size() == 1)
            return new WorldBBox3d(line.start(), line.end());
        WorldCoords3d[] ends = new WorldCoords3d[lines.size()];
        for (int i = 0; i < lines.size(); i++)
            ends[i] = lines.get(i).end();
        return new WorldBBox3d(line.start(), ends);
    }

    /**
     * Creates a new polyline connecting all points in the given list.
     *
     * @param points the points of the polyline.
     * @return a new polyline.
     */
    public static PolyLine3d fromPoints(List<WorldCoords3d> points) {
        if (points.size() < 2)
            return new PolyLine3d(Collections.emptyList());
        if (points.size() == 2)
            return new PolyLine3d(Collections.singletonList(new Line3d(points.get(0), points.get(1))));
        List<Line3d> lines = new ArrayList<>();
        for (int i = 1; i < points.size(); i++)
            lines.add(new Line3d(points.get(i - 1), points.get(i)));
        return new PolyLine3d(lines);
    }

    /**
     * Creates a new polyline connecting all the given points.
     *
     * @param points the points of the polyline.
     * @return a new polyline.
     */
    public static PolyLine3d fromPoints(WorldCoords3d... points) {
        return fromPoints(Arrays.asList(points));
    }

    @Override
    public Iterable<LineVoxel3d> borderVoxels() {
        return () -> new MultiIterator<>(new RemapIterator<>(lines, Shape3d::borderVoxels));
    }

    @Override
    public Iterable<Voxel3d> insideVoxels() {
        return () -> Collections.emptyIterator();
    }

}
