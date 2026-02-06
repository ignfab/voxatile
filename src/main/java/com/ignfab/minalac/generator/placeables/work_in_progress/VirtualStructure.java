package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;
import com.ignfab.minalac.generator.world.VoxelTile;

public class VirtualStructure implements Structure {
    Structure[][][] structures;
    IndexMapper axisX;
    IndexMapper axisY;
    IndexMapper axisZ;
    WorldBBox3d limits;

    public VirtualStructure(Structure[][][] structures, IndexMapper axisX, IndexMapper axisY, IndexMapper axisZ) {
        this.structures = structures;
        this.axisX = axisX;
        this.axisY = axisY;
        this.axisZ = axisZ;
        this.limits = new WorldBBox3d(structures[0][0][0].limits().min(), new WorldSize3d(
            axisX.length__maybeWrong(),
            axisY.length__maybeWrong(),
            axisZ.length__maybeWrong()
        ));
        // TODO : ou alors c'est tacite que 0,0,0 contient le point d'origine
    }

    @Override
    public Placeable get(int x, int y, int z) {
        IndexMapper.PlaceableIndex aX = axisX.placeable(x);
        IndexMapper.PlaceableIndex aY = axisY.placeable(y);
        IndexMapper.PlaceableIndex aZ = axisZ.placeable(z);
        return structures
            [aX.index()]
            [aY.index()]
            [aZ.index()]
            .get(
                aX.coordinateValue(),
                aY.coordinateValue(),
                aZ.coordinateValue()
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
