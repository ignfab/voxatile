package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.tasks.HeightmapStatsTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link HeightmapStatsTask}.
 */
public class HeightmapStatsTaskParams extends ModelTaskParams {
    /**
     * {@link ReadableHeightmap} used to compute statistics (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams heightmap;

    /**
     * Specifies which statistics to compute (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ComputeParams compute;

    /**
     * Represents statistics to compute.
     */
    public static class ComputeParams {
        /**
         * Metadata where to store computed minimum value (optional).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        @JsonAlias("min")
        public String minimum;

        /**
         * Metadata where to store computed maximum value (optional).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        @JsonAlias("max")
        public String maximum;
    }

    /**
     * Constructor used to ensure that the required fields are present during
     * deserialization.
     *
     * @param heightmap {@link ReadableHeightmap} used to compute statistics
     * @param compute specifies which statistics to compute
     */
    @ConstructorProperties({ "heightmap", "compute" })
    public HeightmapStatsTaskParams(
        ReadableHeightmapParams heightmap,
        ComputeParams compute
    ) {
        this.heightmap = heightmap;
        this.compute = compute;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        heightmap.validate();

        if (compute.minimum == null && compute.maximum == null)
            throw new IllegalArgumentException("The 'compute' field must have at least one optional subfield specified.");
        if (compute.minimum != null && compute.minimum.isBlank())
            throw new IllegalArgumentException("The 'minimum' field cannot be empty or contain only whitespace.");
        if (compute.maximum != null && compute.maximum.isBlank())
            throw new IllegalArgumentException("The 'maximum' field cannot be empty or contain only whitespace.");
    }

    @Override
    public TileTask create(Generation generation) {
        return new HeightmapStatsTask(
            models.create(generation),
            heightmap.create(generation.heightmaps()),
            compute.minimum,
            compute.maximum
        );
    }
}
