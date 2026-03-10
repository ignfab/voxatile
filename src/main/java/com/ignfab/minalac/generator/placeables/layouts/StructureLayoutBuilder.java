package com.ignfab.minalac.generator.placeables.layouts;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
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
        this.axisXBuilder = new ConstantAxisMapperBuilder(structure.limits().sizeX());
        this.axisYBuilder = new ConstantAxisMapperBuilder(structure.limits().sizeY());
        this.axisZBuilder = new ConstantAxisMapperBuilder(structure.limits().sizeZ());
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) {
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

    @Override
    public void checkBuildable(int sizeX, int sizeY, int sizeZ) throws UnbuildableException {
        System.out.println(sizeX + ", " + sizeX + ", " + sizeZ);
        //throw new UnsupportedOperationException("Not implemented yet");
    }
}