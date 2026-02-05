package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelTile;

public class StructureImpl implements Structure {
    Structure[][][] structures;
    IndexMapper axisX;
    IndexMapper axisY;
    IndexMapper axisZ;
    WorldBBox3d limits;

    public StructureImpl(Structure[][][] structures, IndexMapper axisX, IndexMapper axisY, IndexMapper axisZ) {
        this.structures = structures;
        this.axisX = axisX;
        this.axisY = axisY;
        this.axisZ = axisZ;
    }

    @Override
    public Placeable get(int x, int y, int z) {
        return structures
            [axisX.tmp_order_index_getStructImpl(x)]
            [axisY.tmp_order_index_getStructImpl(y)]
            [axisZ.tmp_order_index_getStructImpl(z)]
            .get(
                axisX.tmp_localCoordinate_getStructImpl(x),
                axisY.tmp_localCoordinate_getStructImpl(y),
                axisZ.tmp_localCoordinate_getStructImpl(z)
            );
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
