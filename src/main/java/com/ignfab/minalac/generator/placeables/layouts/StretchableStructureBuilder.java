package com.ignfab.minalac.generator.placeables.layouts;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.placeables.EmptyStructure;
import com.ignfab.minalac.generator.placeables.LayoutStructure;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.utils.axis.Axis;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.AxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.ConstantAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.DelegateAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.StretcherAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

// TODO-Z: There is probably a better way to do it. This is to lock stretchable ability to only PlaceableStructure.
//  Since choice is uncertain, there is redundancy with DefaultLayoutBuilder.
public class StretchableStructureBuilder implements LayoutBuilder {
    private final Structure structure;
    private final AxisMapperBuilder xAxisBuilder;
    private final AxisMapperBuilder yAxisBuilder;
    private final AxisMapperBuilder zAxisBuilder;

    public StretchableStructureBuilder(PlaceableStructure structure, StretchAxis axisX, StretchAxis axisY, StretchAxis axisZ) throws UnbuildableException {
        this.structure = structure;
        AxisMapperBuilder xBase = new ConstantAxisMapperBuilder(structure.limits().sizeX(), structure.limits().minX());
        AxisMapperBuilder yBase = new ConstantAxisMapperBuilder(structure.limits().sizeY(), structure.limits().minY());
        AxisMapperBuilder zBase = new ConstantAxisMapperBuilder(structure.limits().sizeZ(), structure.limits().minZ());

        xAxisBuilder = axisX == null ? new DelegateAxisMapperBuilder(xBase) : createAndCheck(structure.limits(), xBase, axisX);
        yAxisBuilder = axisY == null ? new DelegateAxisMapperBuilder(yBase) : createAndCheck(structure.limits(), yBase, axisY);
        zAxisBuilder = axisZ == null ? new DelegateAxisMapperBuilder(zBase) : createAndCheck(structure.limits(), zBase, axisZ);
    }

    private AxisMapperBuilder createAndCheck(WorldBBox3d limits, AxisMapperBuilder base, StretchAxis axis) throws UnbuildableException {
        if (axis.stretchPosition > limits.max().coord(axis.axis) || axis.stretchPosition < limits.min().coord(axis.axis))
            throw new UnbuildableException("\"at\" value (%d) is outside structure (%d to %d) for axis %s"
                .formatted(axis.stretchPosition, limits.min().coord(axis.axis), limits.max().coord(axis.axis), axis.axis));

        if (axis.minStretch == 0 && limits.size().coord(axis.axis) <= 1)
            throw new UnbuildableException("Forbidden to create empty builder");

        return new StretcherAxisMapperBuilder(base, axis.stretchPosition, axis.minStretch, axis.maxStretch);
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) throws UnbuildableException {
        // TODO-Z: For now, redondant with DefaultLayoutStructure
        AxisMapper axisX = xAxisBuilder.build(sizeX);
        AxisMapper axisY = yAxisBuilder.build(sizeY);
        AxisMapper axisZ = zAxisBuilder.build(sizeZ);


        // TODO-Z: Should not happen as either Constant or Stretch have a length of 1
        if (axisX.intervals().length == 0 || axisY.intervals().length == 0 || axisZ.intervals().length == 0)
            return EmptyStructure.INSTANCE;

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
