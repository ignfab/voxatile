package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelTile;

public class EmptyStructure implements Structure {

    public static final EmptyStructure INSTANCE = new EmptyStructure();

    private EmptyStructure() {};

    @Override
    public void place(VoxelTile tile, int x, int y, int z) {}

    @Override
    public Placeable get(int x, int y, int z) {
        return Nothing.INSTANCE;
    }

    @Override
    public WorldBBox3d limits() {
        return WorldBBox3d.EMPTY;
    }

}
