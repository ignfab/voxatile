package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelTile;


// USELESS: Instead, add a structure params variant for single placeable
public class MonoStructure implements Structure {

    private Placeable placeable;

    public MonoStructure(Placeable placeable) {
        this.placeable = placeable;
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        placeable.place(tile, x, y, z);
    }

    @Override
    public WorldBBox3d limits() {
        return WorldBBox3d.ORIGIN;
    }

    @Override
    public Placeable get(int x, int y, int z) {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }


}
