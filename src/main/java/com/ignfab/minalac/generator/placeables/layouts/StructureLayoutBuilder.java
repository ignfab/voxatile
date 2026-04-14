package com.ignfab.minalac.generator.placeables.layouts;

import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.AxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.ConstantAxisMapperBuilder;

/**
 * A {@link LayoutBuilder} derived from a {@link Structure}.
 * <p>
 * This class allows to use structures in layouts.
 */
public class StructureLayoutBuilder implements LayoutBuilder {
    private final Structure structure;
    private final AxisMapperBuilder xAxisBuilder;
    private final AxisMapperBuilder yAxisBuilder;
    private final AxisMapperBuilder zAxisBuilder;

    /**
     * Creates a new {@code StructureLayoutBuilder} for the given {@link Structure}.
     *
     * @param structure sturcture to create a {@link LayoutBuilder} for.
     */
    public StructureLayoutBuilder(Structure structure) {
        this.structure = structure;
        this.xAxisBuilder = new ConstantAxisMapperBuilder(structure.limits().sizeX(), structure.limits().minX());
        this.yAxisBuilder = new ConstantAxisMapperBuilder(structure.limits().sizeY(), structure.limits().minY());
        this.zAxisBuilder = new ConstantAxisMapperBuilder(structure.limits().sizeZ(), structure.limits().minZ());
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) {
        // checkResizability(sizeX, sizeY, sizeZ);
        // TODO: Apply translation
        return structure;
    }

    @Override
    public AxisMapperBuilder xAxis() {
        return xAxisBuilder;
    }

    @Override
    public AxisMapperBuilder yAxis() {
        return yAxisBuilder;
    }

    @Override
    public AxisMapperBuilder zAxis() {
        return zAxisBuilder;
    }
}
