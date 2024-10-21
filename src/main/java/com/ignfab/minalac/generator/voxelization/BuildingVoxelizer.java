package com.ignfab.minalac.generator.voxelization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ignfab.minalac.generator.models.BuildingSurfaceType;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public class BuildingVoxelizer implements Voxelizer3d {
    private final Map<BuildingSurfaceType, List<PlanarPolygon>> shapes = new HashMap<>();
    private final WorldBBox3d bbox;


    public BuildingVoxelizer(WorldBBox3d bbox) {
        this.bbox = bbox;
    }

    private List<PlanarPolygon> shapes(BuildingSurfaceType type) {
        return shapes.computeIfAbsent(type, k -> new ArrayList<>());
    }

    public void add(BuildingSurfaceType type, PlanarPolygon shape) {
        shapes(type).add(shape);
    }

    public void addAll(BuildingSurfaceType type, List<? extends PlanarPolygon> shapes) {
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
                    PlanarPolygon::iterable
                )
            )
        );
    }

    public Iterable<Positioned3d> surfaces(BuildingSurfaceType type) {
        return () -> bbox.crop(
            Iterables.unwrap(
                Iterables.remap(
                    shapes(type),
                    PlanarPolygon::iterable
                )
            )
        );
    }
}
