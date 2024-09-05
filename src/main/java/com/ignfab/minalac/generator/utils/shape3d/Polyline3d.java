package com.ignfab.minalac.generator.utils.shape3d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.world3d.Bounded3d;
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
 * It consists of a list of lines, not necessary joined and not necessary closed.
 */
public class Polyline3d implements Bounded3d, Shape3d {
    private List<Line3d> lines;
    private WorldBBox3d bbox;

    /**
     * Creates a new polyline from a collection of lines.
     *
     * @param lines the lines in this polyline
     */
    public Polyline3d(List<Line3d> lines) {
        this.lines = lines;
        this.bbox = WorldBBox3d.surrounding(lines);
    }

    /**
     * Returns the bounding box of the polyline.
     *
     * @return the bounding box
     */
    public WorldBBox3d bbox() {
        return bbox;
    }

    /**
     * Returns a list of lines constiuing the polyline.
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
    public Iterable<LineVoxel3d> borderVoxels() {
        return () -> new MultiIterator<>(new RemapIterator<>(lines, Shape3d::borderVoxels));
    }

    @Override
    public Iterable<Voxel3d> insideVoxels() {
        return () -> Collections.emptyIterator();
    }

}
