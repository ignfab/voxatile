package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.CappedManhattanHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;

/**
 * Parameters for {@link CappedManhattanHeightmap}.
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
    public ReadableHeightmap create(Generation generation) {
        return new CappedManhattanHeightmap(
            manhattan.create(generation),
            maximumDistance,
            targetValue
        );
    }
}
