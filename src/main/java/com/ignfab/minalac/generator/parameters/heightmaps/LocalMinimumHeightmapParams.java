package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.computed.UnaryOperationHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.computed.operators.LocalMinimumHeightmapOperator;

/**
 * Parameters for a {@link LocalMinimumHeightmapOperator} {@link UnaryOperationHeightmapSpec}.
 */
public class LocalMinimumHeightmapParams implements ReadableHeightmapParams {
    /**
     * The base heightmap (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams localMin;
    /**
     * The local minimum range (required).
     */
    public int range;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param localMin the base heightmap.
     * @param range the local minimum range.
     */
    @ConstructorProperties({"localMin", "range"})
    public LocalMinimumHeightmapParams(ReadableHeightmapParams localMin, int range) {
        this.localMin = localMin;
        this.range = range;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        localMin.validate();
        if (range < 0)
            throw new IllegalArgumentException("range can not be negative");
    }

    @Override
    public ReadableHeightmapSpec create(HeightmapDeclarationStore store) {
        return new UnaryOperationHeightmapSpec(
            localMin.create(store),
            new LocalMinimumHeightmapOperator(range)
        );
    }
}
