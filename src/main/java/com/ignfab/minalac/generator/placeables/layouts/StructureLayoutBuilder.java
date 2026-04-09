package com.ignfab.minalac.generator.placeables.layouts;

import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.AxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.ConstantAxisMapperBuilder;

public class StructureLayoutBuilder implements LayoutBuilder {
    Structure structure;
    AxisMapperBuilder axisXBuilder;
    AxisMapperBuilder axisYBuilder;
    AxisMapperBuilder axisZBuilder;


    public StructureLayoutBuilder(Structure structure) {
        this.structure = structure;
        this.axisXBuilder = new ConstantAxisMapperBuilder(structure.limits().sizeX(), structure.limits().minX());
        this.axisYBuilder = new ConstantAxisMapperBuilder(structure.limits().sizeY(), structure.limits().minY());
        this.axisZBuilder = new ConstantAxisMapperBuilder(structure.limits().sizeZ(), structure.limits().minZ());
    }

    @Override
    public Structure build(Integer sizeX, Integer sizeY, Integer sizeZ) {
        // checkResizability(sizeX, sizeY, sizeZ);
        // TODO: Apply translation
        return structure;
    }

    @Override
    public AxisMapperBuilder axisX() {
        return axisXBuilder;
    }

    @Override
    public AxisMapperBuilder axisY() {
        return axisYBuilder;
    }

    @Override
    public AxisMapperBuilder axisZ() {
        return axisZBuilder;
    }
}