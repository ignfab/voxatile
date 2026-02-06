package com.ignfab.minalac.generator.placeables.work_in_progress;

public class StretchedStructureBuilder extends ResizedStructureBuilderImpl {
    private StretchedStructureBuilder(ResizedStructureBuilder structureBuilder, IndexMapperBuilder axisXBuilder, IndexMapperBuilder axisYBuilder, IndexMapperBuilder axisZBuilder) {
        super(structureBuilder, axisXBuilder, axisYBuilder, axisZBuilder);
    }

    public static ResizedStructureBuilder STRETCHED(ResizedStructureBuilder builder, Integer elasticAtX, Integer elasticAtY, Integer elasticAtZ) {
        IndexMapperBuilder axisXBuilder = (elasticAtX == null) ? new IndexMapperBuilder.Identity(builder.axisX().minimalLength()) : new IndexMapperBuilder.Stretcher(elasticAtX, builder.axisX().minimalLength() - 1);
        IndexMapperBuilder axisYBuilder = (elasticAtY == null) ? new IndexMapperBuilder.Identity(builder.axisY().minimalLength()) : new IndexMapperBuilder.Stretcher(elasticAtY, builder.axisY().minimalLength() - 1);
        IndexMapperBuilder axisZBuilder = (elasticAtZ == null) ? new IndexMapperBuilder.Identity(builder.axisZ().minimalLength()) : new IndexMapperBuilder.Stretcher(elasticAtZ, builder.axisZ().minimalLength() - 1);
        return new StretchedStructureBuilder(builder, axisXBuilder, axisYBuilder, axisZBuilder);
    }
    /*

    public StretchedStructureBuilder(ResizedStructureBuilder structureBuilder, Integer elasticAtX) {
        this.structureBuilder = structureBuilder;

        if (elasticAtX == null)
            axisXBuilder = new IndexMapperBuilder.Identity(structureBuilder.axisX().minimalLength());
        else
            axisXBuilder = new IndexMapperBuilder.Stretcher(elasticAtX, structureBuilder.axisX().minimalLength() - 1);


        axisYBuilder = new IndexMapperBuilder.Identity(structureBuilder.axisY().minimalLength());
        axisZBuilder = new IndexMapperBuilder.Identity(structureBuilder.axisZ().minimalLength());
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) {
        IndexMapper ax, aY, aZ;
        ax = axisXBuilder.build(sizeX);
        aY = axisYBuilder.build(sizeX);
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
    }*/
}
