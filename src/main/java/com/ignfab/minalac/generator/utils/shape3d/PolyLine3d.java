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
 */
public class PolyLine3d implements Shape3d {
    private final List<Line3d> lines;
    private final WorldBBox3d bbox;

    /**
     * Creates a {@code PolyLine3d}.
     *
     * @param lines the lines in this polyline.
     */

    public PolyLine3d(List<Line3d> lines) {
        this.lines = lines;

        // Compute bouning box of all the lines
        if (lines.isEmpty())
            bbox = WorldBBox3d.EMPTY;
        else {
            Line3d line = lines.get(0);
            if (lines.size() == 1)
                bbox = line.bbox();
            else {
                WorldCoords3d[] ends = new WorldCoords3d[lines.size()];
                for (int i = 0; i < lines.size(); i++)
                    ends[i] = lines.get(i).end();
                bbox = new WorldBBox3d(line.start(), ends);
            }
        }
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

    /**
     * Returns list of lines constituing this polyline.
     *
     * @return list of lines
     */
    public List<Line3d> lines() {
        return lines;
    };

    @Override
    public WorldBBox3d bbox() {
        return bbox;
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
