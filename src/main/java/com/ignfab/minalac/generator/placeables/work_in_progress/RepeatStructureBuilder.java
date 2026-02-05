package com.ignfab.minalac.generator.placeables.work_in_progress;

public class RepeatStructureBuilder implements StructureBuilder {
    StructureBuilder structureBuilder;
    IndexMapperBuilder axisXBuilder;
    IndexMapperBuilder axisYBuilder;
    IndexMapperBuilder axisZBuilder;

    public RepeatStructureBuilder(StructureBuilder structureBuilder) {
        this.structureBuilder = structureBuilder;
        axisXBuilder = new IndexMapperBuilder.LastAll();
        axisYBuilder = new IndexMapperBuilder.Identity(structureBuilder.axisY().ask(0));
        axisZBuilder = new IndexMapperBuilder.Identity(structureBuilder.axisZ().ask(0));
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) {
        IndexMapper ax, aY, aZ;
        ax = axisXBuilder.build(sizeX);
        aY = axisYBuilder.build(sizeX);
        aZ = axisZBuilder.build(sizeZ);
        Structure[][][] tab = new Structure
            [ax.structureIndex__toBeChanged().size()]
            [aY.structureIndex__toBeChanged().size()]
            [aZ.structureIndex__toBeChanged().size()];
        for (IndexMapper.StructureIndex iX : ax.structureIndex__toBeChanged()) {
            for (IndexMapper.StructureIndex iY : aY.structureIndex__toBeChanged()) {
                for (IndexMapper.StructureIndex iZ : aZ.structureIndex__toBeChanged()) {
                    tab[iX.order()][iY.order()][iZ.order()] = structureBuilder.build(iX.length(), iY.length(), iZ.length());
                }
            }
        }

        return (new StructureImpl(tab, ax, aY, aZ));
        // throw new RuntimeException("Not implemented");
    }

    @Override
    public IndexMapperBuilder axisX() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public IndexMapperBuilder axisY() {
        return axisYBuilder;
    }

    @Override
    public IndexMapperBuilder axisZ() {
        throw new RuntimeException("Not implemented");
    }
}
