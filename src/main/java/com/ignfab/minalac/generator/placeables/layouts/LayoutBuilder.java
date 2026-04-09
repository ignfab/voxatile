package com.ignfab.minalac.generator.placeables.layouts;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.utils.axis.mappers.builders.AxisMapperBuilder;

/**
 * A structure layout builder based on axis mappers.
 */
public interface LayoutBuilder {
    /**
     * Builds a structure of given size.
     *
     * @param sizeX Size along x-axis
     * @param sizeY Size along y-axis
     * @param sizeZ Size along z-axis
     *
     * @return built structure
     *
     * @throws UnbuildableException if structure cannot be built
     */
    Structure build(Integer sizeX, Integer sizeY, Integer sizeZ) throws UnbuildableException;

    /**
     * @return X-axis mapper builder.
     */
    AxisMapperBuilder xAxis();

    /**
     * @return Y-axis mapper builder.
     */
    AxisMapperBuilder yAxis();

    /**
     * @return Z-axis mapper builder.
     */
    AxisMapperBuilder zAxis();
}
