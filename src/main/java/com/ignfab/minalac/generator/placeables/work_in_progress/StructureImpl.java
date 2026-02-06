package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
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
        this.limits = new WorldBBox3d(structures[0][0][0].limits().min(), new WorldSize3d(
            axisX.structure().size(),
            axisY.structure().size(),
            axisZ.structure().size()
        ));
        // TODO : ou alors c'est tacite que 0,0,0 contient le point d'origine
    }

    @Override
    public Placeable get(int x, int y, int z) {
        IndexMapper.PlaceableIndex aX = axisX.placeable(x);
        IndexMapper.PlaceableIndex aY = axisX.placeable(y);
        IndexMapper.PlaceableIndex aZ = axisX.placeable(z);
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
