package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.models.BuildingSurfaceType;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineVoxel3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Shape3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BuildingVoxelizer implements Voxelizer3d {
    private final Map<BuildingSurfaceType, List<Shape3d>> shapes = new HashMap<>();
    private final WorldBBox3d bbox;

    public BuildingVoxelizer(WorldBBox3d bbox) {
        this.bbox = bbox;
    }

    private List<Shape3d> shapes(BuildingSurfaceType type) {
        return shapes.computeIfAbsent(type, k -> new ArrayList<>());
    }

    public void add(BuildingSurfaceType type, Shape3d shape) {
        shapes(type).add(shape);
    }

    public void addAll(BuildingSurfaceType type, List<? extends Shape3d> shapes) {
        shapes(type).addAll(shapes);
    }

    @Override
    public Iterator<Positioned3d> iterator() {
        return bbox.crop(
            Iterators.unwrapIterables(
                Iterators.remap(
                    Iterators.unwrapIterables(
                        shapes.values().iterator()
                    ),
                    Shape3d::allVoxels
                )
            )
        );
    }

    public Iterable<Positioned3d> surfaces(BuildingSurfaceType type) {
        return () -> bbox.crop(
            Iterables.unwrap(
                Iterables.remap(
                    shapes(type),
                    Shape3d::allVoxels
                )
            )
        );
    }

    public Iterable<LineVoxel3d> wireframe() {
        return Iterables.unwrap(
            Iterables.remap(
                Iterables.unwrap(
                    shapes.values()
                ),
                Shape3d::borderVoxels
            )
        );
    }
}
