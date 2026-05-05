package com.ignfab.minalac.generator.placeables.layouts;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.placeables.EmptyStructure;
import com.ignfab.minalac.generator.placeables.LayoutStructure;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.utils.axis.Axis;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.AxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.ConstantAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.StretcherAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public class FooStructureBuilder implements LayoutBuilder {
    private final Structure structure;
    private AxisMapperBuilder xAxisBuilder;
    private AxisMapperBuilder yAxisBuilder;
    private AxisMapperBuilder zAxisBuilder;

    public FooStructureBuilder(Structure structure, StretchAxis axisX, StretchAxis axisY, StretchAxis axisZ) throws UnbuildableException {
        this.structure = structure;
        AxisMapperBuilder xBase = new ConstantAxisMapperBuilder(structure.limits().sizeX(), structure.limits().minX());
        AxisMapperBuilder yBase = new ConstantAxisMapperBuilder(structure.limits().sizeY(), structure.limits().minY());
        AxisMapperBuilder zBase = new ConstantAxisMapperBuilder(structure.limits().sizeZ(), structure.limits().minZ());

        xAxisBuilder = axisX == null ? xBase : foo(structure.limits(), xBase, axisX);
        yAxisBuilder = axisY == null ? yBase : foo(structure.limits(), yBase, axisY);
        zAxisBuilder = axisZ == null ? zBase : foo(structure.limits(), zBase, axisZ);


    }

    private AxisMapperBuilder foo(WorldBBox3d limits, AxisMapperBuilder base, StretchAxis axis) throws UnbuildableException {
        // TODO: Check copy paste
        if (axis.stretchPosition > limits.max().coord(axis.axis) || axis.stretchPosition < limits.min().coord(axis.axis))
            throw new UnsupportedOperationException("\"at\" value (%d) is outside structure (%d to %d) for axis %s"
                .formatted(axis.stretchPosition, limits.min().coord(axis.axis), limits.max().coord(axis.axis), axis.axis));

        if (axis.minStretch == 0 && limits.size().coord(axis.axis) <= 1)
            throw new UnsupportedOperationException("Forbidden to create empty builder");

        return new StretcherAxisMapperBuilder(base, axis.stretchPosition, axis.minStretch, axis.maxStretch);
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) throws UnbuildableException {
        AxisMapper axisX = xAxisBuilder.build(sizeX);
        AxisMapper axisY = yAxisBuilder.build(sizeY);
        AxisMapper axisZ = zAxisBuilder.build(sizeZ);

        if (axisX.intervals().length == 0 || axisY.intervals().length == 0 || axisZ.intervals().length == 0)
            return EmptyStructure.INSTANCE;

        // xisX.intervals().length should always be 1 (For constant & stretched)
        Structure[][][] structures = new Structure[1][1][1];
        structures[0][0][0] = structure;

        return new LayoutStructure(structures, axisX, axisY, axisZ);
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

    public record StretchAxis(Axis axis, int stretchPosition, int minStretch, int maxStretch) { }
}
