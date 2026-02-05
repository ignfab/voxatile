package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;
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
        this.limits = new WorldBBox3d(structures[0][0][0].limits().min(), new WorldSize3d(axisX.tmp_length(), axisY.tmp_length(), axisZ.tmp_length()));
        // TODO : ou alors c'est tacite que 0,0,0 contient le point d'origine
    }

    public StructureImpl(Structure[][][] structures, IndexMapper axisX, IndexMapper axisY, IndexMapper axisZ, WorldCoords3d origin) {
        this.structures = structures;
        this.axisX = axisX;
        this.axisY = axisY;
        this.axisZ = axisZ;
        this.limits =  new WorldBBox3d(origin, new WorldSize3d(axisX.tmp_length(), axisY.tmp_length(), axisZ.tmp_length()));
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
        return limits;
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        throw new RuntimeException("Not implemented");
    }
}
