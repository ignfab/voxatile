package com.ignfab.minalac.generator.placeables.work_in_progress;

import java.util.List;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

public class NewConcatPOCBis implements ResizedStructureBuilder {
    List<ResizedStructureBuilder> builders;
    IndexMapperBuilder axisX;
    IndexMapperBuilder axisY;
    IndexMapperBuilder axisZ;
    Hello rsb;

    public NewConcatPOCBis(List<ResizedStructureBuilder> builders) {
        this.builders = builders;
        ResizedStructureBuilder builderAC = builders.get(0);
        ResizedStructureBuilder builderB = builders.get(1);
        int minY = Math.max(builderAC.axisY().minimalLength(), builderB.axisY().minimalLength());
        int minZ = Math.max(builderAC.axisZ().minimalLength(), builderB.axisZ().minimalLength());

        axisX = new IndexMapperBuilder.MiddleTakesAll(builderAC.axisX().minimalLength());
        axisY = new IndexMapperBuilder.Identity(minY);
        axisZ = new IndexMapperBuilder.Identity(minZ);
        rsb = (i, j, k) -> (i == 1) ? builders.get(1) : builders.get(0);
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) {
        // Tout ça c'est pour faire le tableau de Builders
        IndexMapper aX = axisX.build(sizeX);
        IndexMapper aY = axisY.build(sizeY);
        IndexMapper aZ = axisZ.build(sizeZ);
        ResizedStructureBuilder[][][] structureBuilders = new ResizedStructureBuilder
            [aX.structure().size()]
            [aY.structure().size()]
            [aZ.structure().size()];
        for (IndexMapper.StructureIndex aXi : aX.structure()) {
            for (IndexMapper.StructureIndex aYi : aY.structure()) {
                for (IndexMapper.StructureIndex aZi : aZ.structure()) {
                    structureBuilders[aXi.index()][aYi.index()][aZi.index()] = rsb.whichOne(aXi.index(), aYi.index(), aZi.index());
                }
            }
        }
        return new VirtualStructure(structureBuilders, aX, aY, aZ);
    }

    private static int indexesBuilderToIndexList(int iX, int iY, int iZ) {
        if (iX == 1)
            return 1;
        return 0;
    }

    @Override
    public IndexMapperBuilder axisX() {
        return axisX;
    }

    @Override
    public IndexMapperBuilder axisY() {
        return axisY;
    }

    @Override
    public IndexMapperBuilder axisZ() {
        return axisZ;
    }

    @FunctionalInterface
    interface Hello {
        ResizedStructureBuilder whichOne(int i, int j, int k);
    }
}
