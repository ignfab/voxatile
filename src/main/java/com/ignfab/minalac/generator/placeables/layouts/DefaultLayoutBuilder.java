package com.ignfab.minalac.generator.placeables.layouts;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.placeables.EmptyStructure;
import com.ignfab.minalac.generator.placeables.LayoutStructure;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.utils.axis.Axis;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.AdjustAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.AxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.DelegateAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.KeepAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.PriorityRepartitionAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.RepeatAxisMapperBuilder;

/**
 * A versatile implementation of {@link LayoutBuilder} based on a layout builder provider.
 */
public class DefaultLayoutBuilder implements LayoutBuilder {
    private final LayoutBuilderProvider provider;
    private final AxisMapperBuilder xAxisBuilder;
    private final AxisMapperBuilder yAxisBuilder;
    private final AxisMapperBuilder zAxisBuilder;

    /**
     * Creates a new {@code DefaultLayoutBuilder} with only one, eventually repeated, structure builder.
     *
     * @param builder underlying unique builder
     * @param xAxisBuilder axis builder for x-axis
     * @param yAxisBuilder axis builder for y-axis
     * @param zAxisBuilder axis builder for z-axis
     */
    public DefaultLayoutBuilder(LayoutBuilder builder, AxisMapperBuilder xAxisBuilder, AxisMapperBuilder yAxisBuilder, AxisMapperBuilder zAxisBuilder) {
        // Could be optimized if builder is a DefaultLayoutBuilder, we could simply do:
        // this.provider = builder.provider
        // this.axis*Builder = combine axis*Builder over builder.axis*Builder (need a new method)
        this((x, y, z) -> builder, xAxisBuilder, yAxisBuilder, zAxisBuilder);
    }

    private DefaultLayoutBuilder(LayoutBuilderProvider provider, AxisMapperBuilder xAxisBuilder, AxisMapperBuilder yAxisBuilder, AxisMapperBuilder zAxisBuilder) {
        this.provider = provider;
        this.xAxisBuilder = xAxisBuilder;
        this.yAxisBuilder = yAxisBuilder;
        this.zAxisBuilder = zAxisBuilder;
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) throws UnbuildableException {
        AxisMapper axisX = xAxisBuilder.build(sizeX);
        AxisMapper axisY = yAxisBuilder.build(sizeY);
        AxisMapper axisZ = zAxisBuilder.build(sizeZ);

        int[] intervalsX = axisX.intervals();
        int[] intervalsY = axisY.intervals();
        int[] intervalsZ = axisZ.intervals();

        if (intervalsX.length == 0 || intervalsY.length == 0 || intervalsZ.length == 0)
            return EmptyStructure.INSTANCE;

        Structure[][][] structures = new Structure
            [intervalsX.length]
            [intervalsY.length]
            [intervalsZ.length];

        for (int iX = 0; iX < intervalsX.length; iX++) {
            for (int iY = 0; iY < intervalsY.length; iY++) {
                for (int iZ = 0; iZ < intervalsZ.length; iZ++) {
                    LayoutBuilder b = provider.get(iX, iY, iZ);
                    structures[iX][iY][iZ] = b.build(intervalsX[iX], intervalsY[iY], intervalsZ[iZ]);
                }
            }
        }

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
     * Creates a new {@code LayoutBuilder} repeating an {@link LayoutBuilder} along an axis.
     *
     * @param builder builder to repeat
     * @param axis axis of repetition
     * @param minimum minimal number of repetitions (could be 0)
     * @param maximum maximal number of repetitions
     * @return created {@link LayoutBuilder}
     * @throws UnbuildableException if layout builder cannot be created
     */
    public static LayoutBuilder repeat(LayoutBuilder builder, Axis axis, int minimum, int maximum) throws UnbuildableException {
        if (minimum < 0)
            throw new IllegalArgumentException("minimum must be positive or zero");
        if (minimum > maximum)
            throw new IllegalArgumentException("minimum must be less than maximum");

        return new DefaultLayoutBuilder(
            builder,
            // RepeatAxisMapperBuilder for chosen axis, DelegateAxisMapperBuilder for others
            axis == Axis.X ? new RepeatAxisMapperBuilder(builder.xAxis(), minimum, maximum) : new DelegateAxisMapperBuilder(builder.xAxis()),
            axis == Axis.Y ? new RepeatAxisMapperBuilder(builder.yAxis(), minimum, maximum) : new DelegateAxisMapperBuilder(builder.yAxis()),
            axis == Axis.Z ? new RepeatAxisMapperBuilder(builder.zAxis(), minimum, maximum) : new DelegateAxisMapperBuilder(builder.zAxis())
        );
    }

    /**
     * Creates a new {@code LayoutBuilder} concatenating {@link LayoutBuilder}s along an axis, with priorities for repartition.
     *
     * @param builders builders to concatenate
     * @param axis axis of concatenation
     * @param priorities priorities for each builders (must have same length as {@code builders})
     * @param adjustX whether X-axis should be adjusted or not.
     * @param adjustY whether Y-axis should be adjusted or not.
     * @param adjustZ whether Z-axis should be adjusted or not.
     * @return created {@link LayoutBuilder}
     * @throws UnbuildableException if layout builder cannot be created
     */
    public static LayoutBuilder concat(LayoutBuilder[] builders, Axis axis, int[] priorities, boolean adjustX, boolean adjustY, boolean adjustZ) throws UnbuildableException {
        if (builders.length == 0 || builders.length != priorities.length)
            throw new IllegalArgumentException("array length do not match");

        // Separate in three axis arrays the axes from each builder.
        AxisMapperBuilder[] arrayX = new AxisMapperBuilder[builders.length];
        AxisMapperBuilder[] arrayY = new AxisMapperBuilder[builders.length];
        AxisMapperBuilder[] arrayZ = new AxisMapperBuilder[builders.length];

        for (int i = 0; i < builders.length; i++) {
            arrayX[i] = builders[i].xAxis();
            arrayY[i] = builders[i].yAxis();
            arrayZ[i] = builders[i].zAxis();
        }

        LayoutBuilderProvider provider = switch (axis) {
            case X -> (x, y, z) -> builders[x];
            case Y -> (x, y, z) -> builders[y];
            case Z -> (x, y, z) -> builders[z];
        };

        return new DefaultLayoutBuilder(
            // A LayoutBuilderProvider that maps `builders` argument array to the chosen axis:
            provider,
            // PriorityRepartitionAxisMapperBuilder for chosen axis, KeepAxisMapperBuilder or AdjustAxisMapperBuilder for others
            axis == Axis.X ? new PriorityRepartitionAxisMapperBuilder(arrayX, priorities)
                : adjustX ? new AdjustAxisMapperBuilder(arrayX) : new KeepAxisMapperBuilder(arrayX),
            axis == Axis.Y ? new PriorityRepartitionAxisMapperBuilder(arrayY, priorities)
                : adjustY ? new AdjustAxisMapperBuilder(arrayY) : new KeepAxisMapperBuilder(arrayY),
            axis == Axis.Z ? new PriorityRepartitionAxisMapperBuilder(arrayZ, priorities)
                : adjustZ ? new AdjustAxisMapperBuilder(arrayZ) : new KeepAxisMapperBuilder(arrayZ)
        );
    }

    @FunctionalInterface
    private interface LayoutBuilderProvider {
        LayoutBuilder get(int ix, int iy, int iz);
    }
}
