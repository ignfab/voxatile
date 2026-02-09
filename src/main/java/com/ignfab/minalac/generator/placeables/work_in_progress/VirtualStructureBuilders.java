package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;
import com.ignfab.minalac.generator.world.VoxelTile;

public class VirtualStructureBuilders implements Structure {
    Structure[][][] structuresVirtual;
    IndexMapper axisX;
    IndexMapper axisY;
    IndexMapper axisZ;
    WorldBBox3d limits;

    public VirtualStructureBuilders(ResizedStructureBuilder[][][] builders, IndexMapper axisX, IndexMapper axisY, IndexMapper axisZ) {
        this.axisX = axisX;
        this.axisY = axisY;
        this.axisZ = axisZ;

        structuresVirtual = new Structure[axisX.structure().size()][axisY.structure().size()][axisZ.structure().size()];

        for (IndexMapper.StructureIndex aSx : axisX.structure()) {
            for (IndexMapper.StructureIndex aSy : axisY.structure()) {
                for (IndexMapper.StructureIndex aSz : axisZ.structure()) {
                    ResizedStructureBuilder b = builders[aSx.index()][aSy.index()][aSz.index()];
                    DefaultResizedStructureBuilder bb = new DefaultResizedStructureBuilder(b, b.axisX(), b.axisY(), b.axisZ());
                    structuresVirtual[aSx.index()][aSy.index()][aSz.index()] = bb.build(aSx.length(), aSy.length(), aSz.length());
                }
            }
        }

        limits = new WorldBBox3d(new WorldCoords3d(0,0,0), new WorldSize3d(axisX.length__maybeWrong(), axisY.length__maybeWrong(), axisZ.length__maybeWrong()));
    }

    @Override
    public Placeable get(int x, int y, int z) {
        IndexMapper.PlaceableIndex aX = axisX.placeable(x);
        IndexMapper.PlaceableIndex aY = axisY.placeable(y);
        IndexMapper.PlaceableIndex aZ = axisZ.placeable(z);
        return structuresVirtual[aX.index()][aY.index()][aZ.index()].get(aX.coordinateValue(), aY.coordinateValue(), aZ.coordinateValue());
    }

    @Override
    public WorldBBox3d limits() {
        return limits;
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z) {

    }
}
