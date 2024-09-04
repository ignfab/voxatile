package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
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
 * It consists of a list of lines where the end
 * of one must be the start of the next one.
 * There is no distinction between a closed and an opened polyline.
 */
public class PolyLine2d implements Shape2d {
    private final List<Line2d> lines;
    private final WorldBBox2d bbox;

    /**
     * Creates a {@code PolyLine2d}.
     *
     * @param lines the lines in this polyline.
     */

    public PolyLine2d(List<Line2d> lines) {
        this.lines = lines;

        // Compute bouning box of all the lines
        if (lines.isEmpty())
            bbox = WorldBBox2d.EMPTY;
        else {
            Line2d line = lines.get(0);
            if (lines.size() == 1)
                bbox = line.bbox();
            else {
                WorldCoords2d[] ends = new WorldCoords2d[lines.size()];
                for (int i = 0; i < lines.size(); i++)
                    ends[i] = lines.get(i).end();
                bbox = new WorldBBox2d(line.start(), ends);
            }
        }
    }

    /**
     * Creates a new polyline connecting all points in the given list.
     *
     * @param points the points of the polyline.
     * @return a new polyline.
     */
     public static PolyLine2d fromPoints(List<WorldCoords2d> points) {
        if (points.size() < 2)
            return new PolyLine2d(Collections.emptyList());
        if (points.size() == 2)
            return new PolyLine2d(Collections.singletonList(new Line2d(points.get(0), points.get(1))));
        List<Line2d> lines = new ArrayList<>();
        for (int i = 1; i < points.size(); i++)
            lines.add(new Line2d(points.get(i - 1), points.get(i)));
        return new PolyLine2d(lines);
    }

    /**
     * Creates a new polyline connecting all the given points.
     *
     * @param points the points of the polyline.
     * @return a new polyline.
     */
    public static PolyLine2d fromPoints(WorldCoords2d... points) {
        return fromPoints(Arrays.asList(points));
    }

    /**
     * Returns list of lines constituing this polyline.
     *
     * @return list of lines
     */
    public List<Line2d> lines() {
        return lines;
    };

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
        return () -> Collections.emptyIterator();
    }
}
