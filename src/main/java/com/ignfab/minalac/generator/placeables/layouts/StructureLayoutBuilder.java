package com.ignfab.minalac.generator.placeables.layouts;

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

/**
 * This makes a {@link PlaceableStructure} resizable by allowing at most one stretchable band of the structure per axis (e.g. a column at x = 2 and a row at z = 1), effectively making it a Layout Builder.
 */
public class StructureLayoutBuilder implements LayoutBuilder {
    private final Structure structure;
    private final AxisMapperBuilder xAxisBuilder;
    private final AxisMapperBuilder yAxisBuilder;
    private final AxisMapperBuilder zAxisBuilder;

    /**
     * Creates a new {@link StructureLayoutBuilder}.
     *
     * @param structure the {@link PlaceableStructure} to make resizable.
     * @param axisX the band along x-axis to make stretchable. A {@code null} value make it not stretchable along x-axis.
     * @param axisY the band along y-axis to make stretchable. A {@code null} value make it not stretchable along y-axis.
     * @param axisZ the band along z-axis to make stretchable. A {@code null} value make it not stretchable along z-axis.
     * @throws UnbuildableLayoutException if unable to create with provided arguments.
     */
    public StructureLayoutBuilder(Structure structure, StretchAxis axisX, StretchAxis axisY, StretchAxis axisZ) throws UnbuildableLayoutException {
        this.structure = structure;
        ConstantAxisMapperBuilder xBase = new ConstantAxisMapperBuilder(structure.limits().sizeX(), structure.limits().minX());
        ConstantAxisMapperBuilder yBase = new ConstantAxisMapperBuilder(structure.limits().sizeY(), structure.limits().minY());
        ConstantAxisMapperBuilder zBase = new ConstantAxisMapperBuilder(structure.limits().sizeZ(), structure.limits().minZ());

        xAxisBuilder = createAndCheck(structure.limits(), xBase, axisX);
        yAxisBuilder = createAndCheck(structure.limits(), yBase, axisY);
        zAxisBuilder = createAndCheck(structure.limits(), zBase, axisZ);
    }

    private AxisMapperBuilder createAndCheck(WorldBBox3d limits, ConstantAxisMapperBuilder base, StretchAxis axis) throws UnbuildableLayoutException {
        if (axis == null)
            return new DelegateAxisMapperBuilder(base);

        if (axis.stretchPosition > limits.max().coord(axis.axis) || axis.stretchPosition < limits.min().coord(axis.axis))
            throw new UnbuildableLayoutException(
                "\"at\" value (%d) is outside structure (%d to %d) for axis %s".formatted(
                    axis.stretchPosition,
                    limits.min().coord(axis.axis),
                    limits.max().coord(axis.axis),
                    axis.axis
                )
            );

        return new StretcherAxisMapperBuilder(base, axis.stretchPosition, axis.minStretch, axis.maxStretch);
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) throws UnbuildableLayoutException {
        // There is redundancy with DefaultLayoutStructure.
        // Fow now, it is accepted as DefaultLayoutStructure is builder to builder and this class structure to builder
        AxisMapper axisX = xAxisBuilder.build(sizeX);
        AxisMapper axisY = yAxisBuilder.build(sizeY);
        AxisMapper axisZ = zAxisBuilder.build(sizeZ);


        // StretcherAxisMapperBuilder can have a size of zero. (Stretchable, length of 1, asked 0)
        if (axisX.intervals().length == 0 || axisY.intervals().length == 0 || axisZ.intervals().length == 0)
            return PlaceableStructure.EMPTY;

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

    /**
     * Defines a stretchable band along a given axis.
     *
     * @param axis the axis which the band is defined.
     * @param stretchPosition the coordinate of the band to stretch. Must be within structure limits.
     * @param minStretch minimum repetitions. May be 0 if the structure size along the given axis is greater than 0. On that particular case, the structure may be squeezed.
     * @param maxStretch maximum repetitions.
     */
    public record StretchAxis(Axis axis, int stretchPosition, int minStretch, int maxStretch) {}
}
