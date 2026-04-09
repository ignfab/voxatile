package com.ignfab.minalac.generator.placeables.layouts;

import java.util.Arrays;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.placeables.EmptyStructure;
import com.ignfab.minalac.generator.placeables.LayoutStructure;
import com.ignfab.minalac.generator.utils.axis.Axis;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.DelegateAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.RepeatAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.AxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.PriorityRepartitionAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.StretcherAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.OverlayAxisMapperBuilder;

/**
 * A versatile implementation of {@link LayoutBuilder} based on a layout builder provider.
 */
public class DefaultLayoutBuilder implements LayoutBuilder {
    private final LayoutBuilderProvider provider;
    private final AxisMapperBuilder axisXBuilder;
    private final AxisMapperBuilder axisYBuilder;
    private final AxisMapperBuilder axisZBuilder;

    /**
     * Creates a new {@code DefaultAxisStructureBuilder} with only one, eventually repeated, structure builder.
     */
    public DefaultLayoutBuilder(LayoutBuilder builder, AxisMapperBuilder axisXBuilder, AxisMapperBuilder axisYBuilder, AxisMapperBuilder axisZBuilder) {
        // Could be optimized if builder is a DefaultLayoutBuilder, we could simply do:
        // this.provider = builder.provider
        // this.axis*Builder = combine axis*Builder over builder.axis*Builder (need a new method)
        this((x, y, z) -> builder, axisXBuilder, axisYBuilder, axisZBuilder);
    }

    private DefaultLayoutBuilder(LayoutBuilderProvider provider, AxisMapperBuilder axisXBuilder, AxisMapperBuilder axisYBuilder, AxisMapperBuilder axisZBuilder) {
        this.provider = provider;
        this.axisXBuilder = axisXBuilder;
        this.axisYBuilder = axisYBuilder;
        this.axisZBuilder = axisZBuilder;
    }

    @Override
    public Structure build(Integer sizeX, Integer sizeY, Integer sizeZ) throws UnbuildableException {
        AxisMapper axisX = sizeX == null ? axisXBuilder.build() : axisXBuilder.build(sizeX);
        AxisMapper axisY = sizeY == null ? axisYBuilder.build() : axisYBuilder.build(sizeY);
        AxisMapper axisZ = sizeZ == null ? axisZBuilder.build() : axisZBuilder.build(sizeZ);

        if (axisX.intervals().length == 0 ||
            axisY.intervals().length == 0 ||
            axisZ.intervals().length == 0)
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
     * @throws UnbuildableException
     */
    public static LayoutBuilder stretch(LayoutBuilder builder, Axis axis, int stretchPosition, int minStretch, int maxStretch) throws UnbuildableException {
        return new DefaultLayoutBuilder(
            builder,
            // StretcherAxisMapperBuilder for chosen axis, DelegateAxisMapperBuilder for others
            axis.x() ? new StretcherAxisMapperBuilder(builder.axisX(), stretchPosition, minStretch, maxStretch) : new DelegateAxisMapperBuilder(builder.axisX()),
            axis.y() ? new StretcherAxisMapperBuilder(builder.axisY(), stretchPosition, minStretch, maxStretch) : new DelegateAxisMapperBuilder(builder.axisY()),
            axis.z() ? new StretcherAxisMapperBuilder(builder.axisZ(), stretchPosition, minStretch, maxStretch) : new DelegateAxisMapperBuilder(builder.axisZ())
        );
    }

    /**
     * Creates a new {@code AxisStructureBuilder} repeating an {@link LayoutBuilder} along an axis
     *
     * @param builder builder to repeat
     * @param axis axis of repetition
     * @param minOccur minimal repetition (could be 0)
     * @throws UnbuildableException
     */
    public static LayoutBuilder repeat(LayoutBuilder builder, Axis axis, int minOccur) throws UnbuildableException {
        return new DefaultLayoutBuilder(
            builder,
            // RepeatAxisMapperBuilder for chosen axis, DelegateAxisMapperBuilder for others
            axis.x() ? new RepeatAxisMapperBuilder(builder.axisX(), minOccur) : new DelegateAxisMapperBuilder(builder.axisX()),
            axis.y() ? new RepeatAxisMapperBuilder(builder.axisY(), minOccur) : new DelegateAxisMapperBuilder(builder.axisY()),
            axis.z() ? new RepeatAxisMapperBuilder(builder.axisZ(), minOccur) : new DelegateAxisMapperBuilder(builder.axisZ())
        );
    }

    /**
     * Creates a new {@code AxisStructureBuilder} concatenating {@link LayoutBuilder}s along an axis, with priorities for repartition.
     *
     * @param builders builders to concatenate
     * @param axis axis of concatenation
     * @param priorities priorities for each builders (must have same length as {@code builders})
     * @throws UnbuildableException
     */
    public static LayoutBuilder priority(LayoutBuilder[] builders, Axis axis, int[] priorities) throws UnbuildableException{
        if (builders.length == 0 || builders.length != priorities.length)
            throw new RuntimeException("tab length do not match");

        // Separate in three axis arrays the axes from each builder.
        AxisMapperBuilder[] tabX = Arrays.stream(builders).map(LayoutBuilder::axisX).toArray(AxisMapperBuilder[]::new);
        AxisMapperBuilder[] tabY = Arrays.stream(builders).map(LayoutBuilder::axisY).toArray(AxisMapperBuilder[]::new);
        AxisMapperBuilder[] tabZ = Arrays.stream(builders).map(LayoutBuilder::axisZ).toArray(AxisMapperBuilder[]::new);

        return new DefaultLayoutBuilder(
            // A LayoutBuilderProvider that maps `builders` argument array to the chosen axis:
            switch (axis) {
                case X -> (x, y, z) -> { return builders[x]; };
                case Y -> (x, y, z) -> { return builders[y]; };
                case Z -> (x, y, z) -> { return builders[z]; };
            },
            // PriorityRepartitionAxisMapperBuilder for chosen axis, SuperDelegateAxisMapperBuilder for others
            axis.x() ? new PriorityRepartitionAxisMapperBuilder(tabX, priorities) : new OverlayAxisMapperBuilder(tabX),
            axis.y() ? new PriorityRepartitionAxisMapperBuilder(tabY, priorities) : new OverlayAxisMapperBuilder(tabY),
            axis.z() ? new PriorityRepartitionAxisMapperBuilder(tabZ, priorities) : new OverlayAxisMapperBuilder(tabZ)
        );
    }

    @FunctionalInterface
    private interface LayoutBuilderProvider {
        LayoutBuilder get(int ix, int iy, int iz);
    }
}
