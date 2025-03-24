package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.LocalMinimumHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;

/**
 * Parameters for a {@link LocalMinimumHeightmap}.
 */
public class LocalMinimumHeightmapParams extends CustomReadableHeightmapParams {
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
    public ReadableHeightmap create(Generation generation) {
        return new LocalMinimumHeightmap(localMin.create(generation), range);
    }
}
