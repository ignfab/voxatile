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

/**
 * This makes a {@link PlaceableStructure} resizable by allowing at most one stretchable band of the structure per axis (e.g. a column at x = 2 and a row at z = 1), effectively making it a Layout Builder.
 */
public class StructureLayoutBuilder extends LayoutBuilder {
    private final Structure structure;

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
        super(
            createAndCheck(structure.limits().minX(), structure.limits().maxX(), axisX),
            createAndCheck(structure.limits().minY(), structure.limits().maxY(), axisY),
            createAndCheck(structure.limits().minZ(), structure.limits().maxZ(), axisZ)
        );
        this.structure = structure;
    }

    private static AxisMapperBuilder createAndCheck(int min, int max, StretchAxis axis) throws UnbuildableLayoutException {
        ConstantAxisMapperBuilder base = new ConstantAxisMapperBuilder(max - min + 1, min);

        if (axis == null)
            return new DelegateAxisMapperBuilder(base);

        if (axis.stretchPosition > max || axis.stretchPosition < min)
            throw new UnbuildableLayoutException(
                "\"at\" value (%d) is outside structure (%d to %d) for axis %s".formatted(
                    axis.stretchPosition, min, max, axis.axis
                )
            );

        return new StretcherAxisMapperBuilder(base, axis.stretchPosition, axis.minStretch, axis.maxStretch);
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) throws UnbuildableLayoutException {
        // There is redundancy with DefaultLayoutStructure.
        // Fow now, it is accepted as DefaultLayoutStructure is builder to builder and this class structure to builder
        AxisMapper axisX = xAxis().build(sizeX);
        AxisMapper axisY = yAxis().build(sizeY);
        AxisMapper axisZ = zAxis().build(sizeZ);


        // StretcherAxisMapperBuilder can have a size of zero. (Stretchable, length of 1, asked 0)
        if (axisX.intervals().length == 0 || axisY.intervals().length == 0 || axisZ.intervals().length == 0)
            return PlaceableStructure.EMPTY;

        Structure[][][] structures = new Structure[1][1][1];
        structures[0][0][0] = structure;

        return new LayoutStructure(structures, axisX, axisY, axisZ);
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
