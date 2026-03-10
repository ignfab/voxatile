package com.ignfab.minalac.generator.placeables.layouts;

import java.util.Arrays;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.placeables.EmptyStructure;
import com.ignfab.minalac.generator.placeables.LayoutStructure;
import com.ignfab.minalac.generator.utils.axis.Axis;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.DelegateAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.EqualizerAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.AxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.PriorityRepartitionAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.StretcherAxisMapperBuilder;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.SuperDelegateAxisMapperBuilder;

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
        this((x, y, z) -> builder, axisXBuilder, axisYBuilder, axisZBuilder);
    }

    private DefaultLayoutBuilder(LayoutBuilderProvider provider, AxisMapperBuilder axisXBuilder, AxisMapperBuilder axisYBuilder, AxisMapperBuilder axisZBuilder) {
        this.provider = provider;
        this.axisXBuilder = axisXBuilder;
        this.axisYBuilder = axisYBuilder;
        this.axisZBuilder = axisZBuilder;
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) throws UnbuildableException {
        AxisMapper axisX, axisY, axisZ;
        axisX = axisXBuilder.build(sizeX);
        axisY = axisYBuilder.build(sizeY);
        axisZ = axisZBuilder.build(sizeZ);

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
    public void checkBuildable(int sizeX, int sizeY, int sizeZ) throws UnbuildableException {
        if (sizeX <= 0)
            throw new UnbuildableException(String.format("sizeX must be strictly positive (Asked : %d)", sizeX));
        if (sizeY <= 0) {
            throw new UnbuildableException(String.format("sizeY must be strictly positive (Asked : %d)", sizeY));
        }if (sizeZ <= 0)
            throw new UnbuildableException(String.format("sizeZ must be strictly positive (Asked : %d)", sizeZ));
        if (axisX().maxSizeUnder(sizeX) != sizeX)
            throw new UnbuildableException(String.format("Asked sizeX (%d) do not match the allowed (%d)", sizeX, axisX().maxSizeUnder(sizeX)));
        if (axisY().maxSizeUnder(sizeY) != sizeY) {
            throw new UnbuildableException(String.format("Asked sizeY (%d) do not match the allowed (%d)", sizeY, axisY().maxSizeUnder(sizeY)));
        }if (axisZ().maxSizeUnder(sizeZ) != sizeZ)
            throw new UnbuildableException(String.format("Asked sizeZ (%d) do not match the allowed (%d)", sizeZ, axisZ().maxSizeUnder(sizeZ)));
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
     */
    public static LayoutBuilder stretch(LayoutBuilder builder, Axis axis, int stretchPosition, int minStretch, int maxStretch) {
        return new DefaultLayoutBuilder(
            builder,
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
     */
    public static LayoutBuilder repeat(LayoutBuilder builder, Axis axis, int minOccur) {
        return new DefaultLayoutBuilder(
            builder,
            axis.x() ? new EqualizerAxisMapperBuilder(builder.axisX(), minOccur) : new DelegateAxisMapperBuilder(builder.axisX()),
            axis.y() ? new EqualizerAxisMapperBuilder(builder.axisY(), minOccur) : new DelegateAxisMapperBuilder(builder.axisY()),
            axis.z() ? new EqualizerAxisMapperBuilder(builder.axisZ(), minOccur) : new DelegateAxisMapperBuilder(builder.axisZ())
        );
    }

    /**
     * Creates a new {@code AxisStructureBuilder} concatenating {@link LayoutBuilder}s along an axis, with priorities for repartition.
     *
     * @param builders builders to concatenate
     * @param axis axis of concatenation
     * @param priorities priorities for each builders (must have same length as {@code builders})
     */
    public static LayoutBuilder priority(LayoutBuilder[] builders, Axis axis, int[] priorities){
        if (builders.length == 0 || builders.length != priorities.length)
            throw new RuntimeException("tab length do not match");
        AxisMapperBuilder[] tabX = Arrays.stream(builders).map(LayoutBuilder::axisX).toArray(AxisMapperBuilder[]::new);
        AxisMapperBuilder[] tabY = Arrays.stream(builders).map(LayoutBuilder::axisY).toArray(AxisMapperBuilder[]::new);
        AxisMapperBuilder[] tabZ = Arrays.stream(builders).map(LayoutBuilder::axisZ).toArray(AxisMapperBuilder[]::new);


        return new DefaultLayoutBuilder(
            switch (axis) {
                case X -> (x, y, z) -> { return builders[x]; };
                case Y -> (x, y, z) -> { return builders[y]; };
                case Z -> (x, y, z) -> { return builders[z]; };
            },
            axis.x() ? new PriorityRepartitionAxisMapperBuilder(tabX, priorities) : new SuperDelegateAxisMapperBuilder(tabX),
            axis.y() ? new PriorityRepartitionAxisMapperBuilder(tabY, priorities) : new SuperDelegateAxisMapperBuilder(tabY),
            axis.z() ? new PriorityRepartitionAxisMapperBuilder(tabZ, priorities) : new SuperDelegateAxisMapperBuilder(tabZ)
        );
    }

    @FunctionalInterface
    private interface LayoutBuilderProvider {
        LayoutBuilder get(int ix, int iy, int iz);
    }
}
