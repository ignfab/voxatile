package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.computed.UnaryOperationHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.computed.operators.CappedManhattanHeightmapOperator;

/**
 * Parameters for a {@link CappedManhattanHeightmapOperator} {@link UnaryOperationHeightmapSpec}.
 */
public class CappedManhattanHeightmapParams implements ReadableHeightmapParams {
    /**
     * The base heightmap (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams manhattan;

    /**
     * The maximum distance. (optional, default: 10)
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int maximumDistance = 10;

    /**
     * The target value. (optional, default: 0)
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int targetValue = 0;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param manhattan the base heightmap.
     */
    @ConstructorProperties({"manhattan"})
    public CappedManhattanHeightmapParams(ReadableHeightmapParams manhattan) {
        this.manhattan = manhattan;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        manhattan.validate();
        if (maximumDistance < 0)
            throw new IllegalArgumentException("Maximum distance must be positive");
    }

    @Override
    public ReadableHeightmapSpec create(HeightmapDeclarationStore store) {
        return new UnaryOperationHeightmapSpec(
            manhattan.create(store),
            new CappedManhattanHeightmapOperator(
                maximumDistance,
                targetValue
            )
        );
    }
}
