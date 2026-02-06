package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;

public class IdentityStructureBuilder implements ResizedStructureBuilder {
    Structure structure;
    IndexMapperBuilder axisXBuilder;
    IndexMapperBuilder axisYBuilder;
    IndexMapperBuilder axisZBuilder;

    public IdentityStructureBuilder(Structure structure) {
        this.structure = structure;
        WorldSize3d size = structure.limits().size();
        axisXBuilder = new IndexMapperBuilder.Identity(size.x());
        axisYBuilder = new IndexMapperBuilder.Identity(size.y());
        axisZBuilder = new IndexMapperBuilder.Identity(size.z());
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) {
        // TODO: revoir ça
        /*
        if (sizeX != axisXBuilder.ask(sizeX) &&
            sizeY != axisYBuilder.ask(sizeY) &&
            sizeZ != axisZBuilder.ask(sizeZ) )
            throw new RuntimeException("NOt buildable");


         */
        Structure[][][] tab = new Structure[1][1][1];
        tab[0][0][0] = structure;

        return new StructureImpl(
            tab,
            axisXBuilder.build(sizeX),
            axisYBuilder.build(sizeY),
            axisZBuilder.build(sizeZ)
        );
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
