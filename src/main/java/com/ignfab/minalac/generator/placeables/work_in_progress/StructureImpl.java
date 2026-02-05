package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelTile;

public class StructureImpl implements Structure {
    @Override
    public Placeable get(int x, int y, int z) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public WorldBBox3d limits() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        throw new RuntimeException("Not implemented");
    }
}
