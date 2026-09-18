package com.ignfab.minalac.generator.placeables.layouts;

import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.AxisMapperBuilder;

/**
 * A structure layout builder based on axis mappers.
 */
public abstract class LayoutBuilder {
    private final AxisMapperBuilder xAxis;
    private final AxisMapperBuilder yAxis;
    private final AxisMapperBuilder zAxis;

    /**
     * Builds a structure of given size.
     *
     * @param sizeX Size along x-axis
     * @param sizeY Size along y-axis
     * @param sizeZ Size along z-axis
     *
     * @return built structure
     *
     * @throws UnbuildableLayoutException if structure cannot be built
     */
    public abstract Structure build(int sizeX, int sizeY, int sizeZ) throws UnbuildableLayoutException;

    /**
     * Creates a new {@code LayoutBuilder}.
     *
     * @param xAxis axis builder for x-axis
     * @param yAxis axis builder for y-axis
     * @param zAxis axis builder for z-axis
     */
    protected LayoutBuilder(
        AxisMapperBuilder xAxis,
        AxisMapperBuilder yAxis,
        AxisMapperBuilder zAxis
    ) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.zAxis = zAxis;
    }

    /**
     * {@return X-axis mapper builder}
     */
    public AxisMapperBuilder xAxis() {
        return xAxis;
    }

    /**
     * {@return Y-axis mapper builder}
     */
    public AxisMapperBuilder yAxis() {
        return yAxis;
    }

    /**
     * {@return Z-axis mapper builder}
     */
    public AxisMapperBuilder zAxis() {
        return zAxis;
    }
}
