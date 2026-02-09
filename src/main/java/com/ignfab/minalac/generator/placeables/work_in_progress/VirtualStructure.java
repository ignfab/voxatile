package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
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

    public VirtualStructure(ResizedStructureBuilder[][][] builders, IndexMapper axisX, IndexMapper axisY, IndexMapper axisZ) {
        this.axisX = axisX;
        this.axisY = axisY;
        this.axisZ = axisZ;

        structures = new Structure[axisX.structure().size()][axisY.structure().size()][axisZ.structure().size()];

        for (IndexMapper.StructureIndex aSx : axisX.structure()) {
            for (IndexMapper.StructureIndex aSy : axisY.structure()) {
                for (IndexMapper.StructureIndex aSz : axisZ.structure()) {
                    ResizedStructureBuilder b = builders[aSx.index()][aSy.index()][aSz.index()];
                    structures[aSx.index()][aSy.index()][aSz.index()] = b.build(aSx.length(), aSy.length(), aSz.length());
                    // TODO: bug, voir pourquoi en emballant j'vais probleme
                    // DefaultResizedStructureBuilder bb = new DefaultResizedStructureBuilder(b, b.axisX(), b.axisY(), b.axisZ());
                    // structures[aSx.index()][aSy.index()][aSz.index()] = bb.build(aSx.length(), aSy.length(), aSz.length());
                }
            }
        }

        this.limits = new WorldBBox3d(structures[0][0][0].limits().min(), new WorldSize3d(
            axisX.length__maybeWrong(),
            axisY.length__maybeWrong(),
            axisZ.length__maybeWrong()
        ));
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
