package com.ignfab.minalac.generator.placeables.layouts;

import java.util.Arrays;
import java.util.List;

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
import com.ignfab.minalac.generator.utils.axis.mappers.builders.StretcherAxisMapperBuilder;

/**
 * A versatile implementation of {@link LayoutBuilder} based on a layout builder provider.
 */
public class DefaultLayoutBuilder implements LayoutBuilder {
    private final LayoutBuilderProvider provider;
    private final AxisMapperBuilder xAxisBuilder;
    private final AxisMapperBuilder yAxisBuilder;
    private final AxisMapperBuilder zAxisBuilder;

    /**
     * Creates a new {@code DefaultAxisStructureBuilder} with only one, eventually repeated, structure builder.
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

        if (axisX.intervals().length == 0 || axisY.intervals().length == 0 || axisZ.intervals().length == 0)
            return EmptyStructure.INSTANCE;

        Structure[][][] structures = new Structure
            [axisX.intervals().length]
            [axisY.intervals().length]
            [axisZ.intervals().length];

        for (int iX = 0; iX < axisX.intervals().length; iX++) {
            for (int iY = 0; iY < axisY.intervals().length; iY++) {
                for (int iZ = 0; iZ < axisZ.intervals().length; iZ++) {
                    LayoutBuilder b = provider.get(iX, iY, iZ);
                    structures[iX][iY][iZ] = b.build(axisX.intervals()[iX], axisY.intervals()[iY], axisZ.intervals()[iZ]);
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
     * Creates a new {@code AxisStructureBuilder} stretching an {@link LayoutBuilder} along an axis.
     * <p>
     * Stetching is done by repeating a stretch position. This repeating is limited by {@code minStretch} and {@code maxStretch}.
     * A stretch of 0 means voxels at stetch position are ommited.
     *
     * @param builder builder to stretch
     * @param axis stretch axis
     * @param stretchPosition where builder should be streched along axis
     * @param minStretch minimal stretching
     * @param maxStretch maximal stretching
     * @return created {@link LayoutBuilder}
     * @throws UnbuildableException if layout builder cannot be created
     */
    public static LayoutBuilder stretch(LayoutBuilder builder, Axis axis, int stretchPosition, int minStretch, int maxStretch) throws UnbuildableException {
        return new DefaultLayoutBuilder(
            builder,
            // StretcherAxisMapperBuilder for chosen axis, DelegateAxisMapperBuilder for others
            axis == Axis.X ? new StretcherAxisMapperBuilder(builder.xAxis(), stretchPosition, minStretch, maxStretch) : new DelegateAxisMapperBuilder(builder.xAxis()),
            axis == Axis.Y ? new StretcherAxisMapperBuilder(builder.yAxis(), stretchPosition, minStretch, maxStretch) : new DelegateAxisMapperBuilder(builder.yAxis()),
            axis == Axis.Z ? new StretcherAxisMapperBuilder(builder.zAxis(), stretchPosition, minStretch, maxStretch) : new DelegateAxisMapperBuilder(builder.zAxis())
        );
        // TODO-Z : Bourrin, comprendre pourquoi remplacement de Delegate par Keep ne marche pas
        /*
        return new DefaultLayoutBuilder(
            builder,
            // StretcherAxisMapperBuilder for chosen axis, DelegateAxisMapperBuilder for others
            axis == Axis.X ? new StretcherAxisMapperBuilder(builder.xAxis(), stretchPosition, minStretch, maxStretch) : new KeepAxisMapperBuilder(builder.xAxis()),
            axis == Axis.Y ? new StretcherAxisMapperBuilder(builder.yAxis(), stretchPosition, minStretch, maxStretch) : new KeepAxisMapperBuilder(builder.yAxis()),
            axis == Axis.Z ? new StretcherAxisMapperBuilder(builder.zAxis(), stretchPosition, minStretch, maxStretch) : new KeepAxisMapperBuilder(builder.zAxis())
        );*/
    }

    /**
     * Creates a new {@code AxisStructureBuilder} repeating an {@link LayoutBuilder} along an axis.
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

        // TODO-Z: A priori le remplacement de Delegate par Keep ça marche, à vérifier en détail

        /*
        return new DefaultLayoutBuilder(
            builder,
            // RepeatAxisMapperBuilder for chosen axis, DelegateAxisMapperBuilder for others
            axis == Axis.X ? new RepeatAxisMapperBuilder(builder.xAxis(), minimum, maximum) : new DelegateAxisMapperBuilder(builder.xAxis()),
            axis == Axis.Y ? new RepeatAxisMapperBuilder(builder.yAxis(), minimum, maximum) : new DelegateAxisMapperBuilder(builder.yAxis()),
            axis == Axis.Z ? new RepeatAxisMapperBuilder(builder.zAxis(), minimum, maximum) : new DelegateAxisMapperBuilder(builder.zAxis())
        );
        */
        return new DefaultLayoutBuilder(
            builder,
            axis == Axis.X ? new RepeatAxisMapperBuilder(builder.xAxis(), minimum, maximum) :  new KeepAxisMapperBuilder(builder.xAxis()),
            axis == Axis.Y ? new RepeatAxisMapperBuilder(builder.yAxis(), minimum, maximum) :  new KeepAxisMapperBuilder(builder.yAxis()),
            axis == Axis.Z ? new RepeatAxisMapperBuilder(builder.zAxis(), minimum, maximum) :  new KeepAxisMapperBuilder(builder.zAxis())
        );
    }


    /**
     * Creates a new {@code AxisStructureBuilder} concatenating {@link LayoutBuilder}s along an axis, with priorities for repartition.
     *
     * @param builders builders to concatenate
     * @param axis axis of concatenation
     * @param priorities priorities for each builders (must have same length as {@code builders})
     * @return created {@link LayoutBuilder}
     * @throws UnbuildableException if layout builder cannot be created
     */
    public static LayoutBuilder concat(LayoutBuilder[] builders, Axis axis, int[] priorities, List<Axis> adjusted) throws UnbuildableException {
        if (builders.length == 0 || builders.length != priorities.length)
            throw new RuntimeException("tab length do not match");

        // Separate in three axis arrays the axes from each builder.
        AxisMapperBuilder[] tabX = Arrays.stream(builders).map(LayoutBuilder::xAxis).toArray(AxisMapperBuilder[]::new);
        AxisMapperBuilder[] tabY = Arrays.stream(builders).map(LayoutBuilder::yAxis).toArray(AxisMapperBuilder[]::new);
        AxisMapperBuilder[] tabZ = Arrays.stream(builders).map(LayoutBuilder::zAxis).toArray(AxisMapperBuilder[]::new);


        // Note: OverlayAxisMapperBuilder est devenu AdjustAxisMapperBuilder (Aussi KeepAxisMapperBuilder d'une certaine manière). C'était dans le overlay qu'il y avait le code smell.
        // TODO-Z : IntelliJ rouge alors que pas besoin.
        LayoutBuilderProvider provider = switch (axis) {
            case X -> (x, y, z) -> builders[x];
            case Y -> (x, y, z) -> builders[y];
            case Z -> (x, y, z) -> builders[z];
        };

        return new DefaultLayoutBuilder(
            // A LayoutBuilderProvider that maps `builders` argument array to the chosen axis:
            provider,
            // PriorityRepartitionAxisMapperBuilder for chosen axis, KeepAxisMapperBuilder or AdjustAxisMapperBuilder for others
            axis == Axis.X ? new PriorityRepartitionAxisMapperBuilder(tabX, priorities)
                : adjusted.contains(Axis.X) ? new AdjustAxisMapperBuilder(tabX) : new KeepAxisMapperBuilder(tabX),
            axis == Axis.Y ? new PriorityRepartitionAxisMapperBuilder(tabY, priorities)
                : adjusted.contains(Axis.Y) ? new AdjustAxisMapperBuilder(tabY) : new KeepAxisMapperBuilder(tabY),
            axis == Axis.Z ? new PriorityRepartitionAxisMapperBuilder(tabZ, priorities)
                : adjusted.contains(Axis.Z) ? new AdjustAxisMapperBuilder(tabZ) : new KeepAxisMapperBuilder(tabZ)
        );
    }

    @FunctionalInterface
    private interface LayoutBuilderProvider {
        LayoutBuilder get(int ix, int iy, int iz);
    }
}
