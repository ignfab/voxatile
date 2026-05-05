package com.ignfab.minalac.generator.placeables.layouts;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.utils.axis.Axis;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.AxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.ConstantAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.StretcherAxisMapperBuilder;

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

    private StructureLayoutBuilder(Structure structure, AxisMapperBuilder xAxisBuilder, AxisMapperBuilder yAxisBuilder, AxisMapperBuilder zAxisBuilder) {
        this.structure = structure;
        this.xAxisBuilder = xAxisBuilder;
        this.yAxisBuilder = yAxisBuilder;
        this.zAxisBuilder = zAxisBuilder;
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) {
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

    public StructureLayoutBuilder stretch(Axis axis, int stretchPosition, int minStretch, int maxStretch) throws UnbuildableException {
        return switch (axis) {
            case X ->
                new StructureLayoutBuilder(
                    this.structure,
                    new StretcherAxisMapperBuilder(this.xAxisBuilder, stretchPosition, minStretch, maxStretch),
                    this.yAxisBuilder,
                    this.zAxisBuilder
                );
            case Y ->
                new StructureLayoutBuilder(
                    this.structure,
                    this.xAxisBuilder,
                    new StretcherAxisMapperBuilder(this.yAxisBuilder, stretchPosition, minStretch, maxStretch),
                    this.zAxisBuilder
                );
            case Z ->
                new StructureLayoutBuilder(
                    this.structure,
                    this.xAxisBuilder,
                    this.yAxisBuilder,
                    new StretcherAxisMapperBuilder(this.zAxisBuilder, stretchPosition, minStretch, maxStretch)
                );
        };
    }

    private AxisMapperBuilder foo(Axis axis, int stretchPosition, int minStretch, int maxStretch) throws UnbuildableException {
        return null;
    }
}
