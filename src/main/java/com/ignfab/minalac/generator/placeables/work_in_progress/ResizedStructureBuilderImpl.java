package com.ignfab.minalac.generator.placeables.work_in_progress;

public abstract class ResizedStructureBuilderImpl implements ResizedStructureBuilder {
    protected ResizedStructureBuilder structureBuilder;
    protected IndexMapperBuilder axisXBuilder;
    protected IndexMapperBuilder axisYBuilder;
    protected IndexMapperBuilder axisZBuilder;

    protected ResizedStructureBuilderImpl(ResizedStructureBuilder structureBuilder, IndexMapperBuilder axisXBuilder, IndexMapperBuilder axisYBuilder, IndexMapperBuilder axisZBuilder) {
        this.structureBuilder = structureBuilder;
        this.axisXBuilder = axisXBuilder;
        this.axisYBuilder = axisYBuilder;
        this.axisZBuilder = axisZBuilder;
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) {
        IndexMapper ax, aY, aZ;
        ax = axisXBuilder.build(sizeX);
        aY = axisYBuilder.build(sizeY);
        aZ = axisZBuilder.build(sizeZ);
        Structure[][][] tab = new Structure
            [ax.structure().size()]
            [aY.structure().size()]
            [aZ.structure().size()];
        for (IndexMapper.StructureIndex iX : ax.structure()) {
            for (IndexMapper.StructureIndex iY : aY.structure()) {
                for (IndexMapper.StructureIndex iZ : aZ.structure()) {
                    tab[iX.index()][iY.index()][iZ.index()] = structureBuilder.build(iX.length(), iY.length(), iZ.length());
                }
            }
        }

        return (new StructureImpl(tab, ax, aY, aZ));
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
