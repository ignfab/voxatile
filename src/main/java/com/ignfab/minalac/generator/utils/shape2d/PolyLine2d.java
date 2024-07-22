package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.IndexedVoxel2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a 2d polyline in the voxel world.
 * It consists of a list of lines where the end
 * of one must be the start of the next one.
 * There is no distinction between a closed and an opened polyline.
 *
 * @param lines the lines in this polyline.
 */
public record PolyLine2d(List<Line2d> lines) implements Iterable<IndexedVoxel2d> {
    /**
     * Computes the bounding box of this polyline.
     * This is the smallest box containing all the lines.
     *
     * @return the bounding box of this polyline.
     */
    public WorldBBox2d bbox() {
        if (lines.isEmpty())
            return WorldBBox2d.EMPTY;
        Line2d line = lines.get(0);
        if (lines.size() == 1)
            return new WorldBBox2d(line.start(), line.end());
        WorldCoords2d[] ends = new WorldCoords2d[lines.size()];
        for (int i = 0; i < lines.size(); i++)
            ends[i] = lines.get(i).end();
        return new WorldBBox2d(line.start(), ends);
    }

    /**
     * Returns an iterator over all voxels in all lines in this polyline.
     *
     * @return a new iterator on this polyline.
     * @see Line2d#iterator()
     */
    @Override
    public Iterator<IndexedVoxel2d> iterator() {
        return new MultiIterator<>(lines);
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
}
