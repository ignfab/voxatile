package com.ignfab.minalac.generator.parameters.tasks.tile;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.placeables.NothingParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.FillBetweenHeightmapAndMetadataTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link FillBetweenHeightmapAndMetadataTask}.
 */
public class FillBetweenHeightmapAndMetadataTaskParams extends ModelTaskParams {
    /**
     * {@code ReadableHeightmap} to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams heightmap;

    /**
     * Name of the model metadata containing the altitude value (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String altitudeMetadata;

    /**
     * {@code Placeable} placed above the altitude value (optional).
     */
    public PlaceableParams placeAbove = new NothingParams();

    /**
     * {@code Placeable} placed below the altitude value (optional).
     */
    public PlaceableParams placeBelow = new NothingParams();

    /**
     * Constructor used to ensure that the required fields are present during
     * deserialization.
     *
     * @param heightmap {@code ReadableHeightmap} to use
     * @param altitudeMetadata name of the model metadata containing the altitude value
     */
    @ConstructorProperties({ "heightmap", "altitudeMetadata" })
    public FillBetweenHeightmapAndMetadataTaskParams(
        ReadableHeightmapParams heightmap,
        String altitudeMetadata
    ) {
        this.heightmap = heightmap;
        this.altitudeMetadata = altitudeMetadata;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        heightmap.validate();
        placeAbove.validate();
        placeBelow.validate();

        if (placeAbove instanceof NothingParams && placeBelow instanceof NothingParams)
            throw new IllegalArgumentException("At least the 'placeAbove' or 'placeBelow' field must be specified.");
        if (altitudeMetadata.isBlank())
            throw new IllegalArgumentException("The 'altitudeMetadata' field cannot be empty or contain only whitespace.");
    }

    @Override
    public TileTask create(Generation generation) {
        return new FillBetweenHeightmapAndMetadataTask(
            models.create(),
            heightmap.create(generation.heightmaps()),
            altitudeMetadata,
            placeAbove.create(generation.seed()),
            placeBelow.create(generation.seed())
        );
    }
}
