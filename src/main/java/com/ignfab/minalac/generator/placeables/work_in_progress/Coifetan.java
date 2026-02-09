package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

public abstract class Coifetan implements ResizedStructureBuilder {
    protected IndexMapperBuilder axisXBuilder;
    protected IndexMapperBuilder axisYBuilder;
    protected IndexMapperBuilder axisZBuilder;
    protected IndexesToResizedStructureBuilder builderProvider;

    protected Coifetan(IndexMapperBuilder axisXBuilder, IndexMapperBuilder axisYBuilder, IndexMapperBuilder axisZBuilder, IndexesToResizedStructureBuilder builderProvider) {
        this.axisXBuilder = axisXBuilder;
        this.axisYBuilder = axisYBuilder;
        this.axisZBuilder = axisZBuilder;
        this.builderProvider = builderProvider;
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) {
        IndexMapper aX = axisXBuilder.build(sizeX);
        IndexMapper aY = axisYBuilder.build(sizeY);
        IndexMapper aZ = axisZBuilder.build(sizeZ);
        ResizedStructureBuilder[][][] structureBuilders = new ResizedStructureBuilder
            [aX.structure().size()]
            [aY.structure().size()]
            [aZ.structure().size()];
        for (IndexMapper.StructureIndex aXi : aX.structure()) {
            for (IndexMapper.StructureIndex aYi : aY.structure()) {
                for (IndexMapper.StructureIndex aZi : aZ.structure()) {
                    structureBuilders[aXi.index()][aYi.index()][aZi.index()] = builderProvider.whichOne(aXi.index(), aYi.index(), aZi.index());
                }
            }
        }
        return new VirtualStructure(structureBuilders, aX, aY, aZ);
    }

    @Override
    public IndexMapperBuilder axisX() {
        return axisXBuilder;
    }

    @Override
    public IndexMapperBuilder axisY() {
        return axisYBuilder;
    }

    @Override
    public IndexMapperBuilder axisZ() {
        return axisZBuilder;
    }
}
