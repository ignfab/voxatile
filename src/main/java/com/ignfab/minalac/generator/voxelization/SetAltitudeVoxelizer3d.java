package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;

/**
 * A 3d voxelizer adding a z-coordinate to voxels from a 2d voxelizer.
 */
public class SetAltitudeVoxelizer3d implements Voxelizer3d {

    private final Voxelizer2d voxelizer;
    private final ReadableHeightmapSpec altitude;

    /**
     * Creates a new {@code SetAltitudeVoxelizer3d}.
     *
     * @param voxelizer 2d voxelizer to get horizontal coordinates from
     * @param altitude heightmap giving the vertical coordinate
     */
    public SetAltitudeVoxelizer3d(Voxelizer2d voxelizer, ReadableHeightmapSpec altitude) {
        this.voxelizer = voxelizer;
        this.altitude = altitude;
    }

    @Override
    public Iterable<? extends Positioned3d> voxelize(Model model) {
        // TODO: This could move into a HeightmapSpec method
        ReadableHeightmap heightmap = GenerationTile.current().heightmap(altitude);

        return Iterables.remap(
            // TODO: If we had voxelizers already filtered by current tile, we wouldn't need to filter here
            heightmap.bbox().filterInside(voxelizer.voxelize(model)),
            (pos) -> pos.coords().to3d(heightmap.get(pos.coords()))
        );
    }
}
