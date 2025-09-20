package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.models.values.ModelValueParams;
import com.ignfab.minalac.generator.parameters.placeables.NothingParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.FillBetweenHeightmapAndValueTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link FillBetweenHeightmapAndValueTask}.
 */
public class FillBetweenHeightmapAndValueTaskParams extends TileTaskParams {
    /**
     * {@code ModelSelection} to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    /**
     * {@code ReadableHeightmap} to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams heightmap;

    /**
     * Model value to use as altitude (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams altitudeValue;

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
     * @param models {@code ModelSelection} to use
     * @param heightmap {@code ReadableHeightmap} to use
     * @param altitudeValue model value to use as altitude
     */
    @ConstructorProperties({ "models", "heightmap", "altitudeValue" })
    public FillBetweenHeightmapAndValueTaskParams(
        ModelSelectionParams models,
        ReadableHeightmapParams heightmap,
        ModelValueParams altitudeValue
    ) {
        this.models = models;
        this.heightmap = heightmap;
        this.altitudeValue = altitudeValue;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        models.validate();
        heightmap.validate();
        placeAbove.validate();
        placeBelow.validate();

        if (placeAbove instanceof NothingParams && placeBelow instanceof NothingParams)
            throw new IllegalArgumentException("At least the 'placeAbove' or 'placeBelow' field must be specified.");
        altitudeValue.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new FillBetweenHeightmapAndValueTask(
            models.create(),
            heightmap.create(generation.heightmaps()),
            altitudeValue.create(generation),
            placeAbove.create(generation.seed()),
            placeBelow.create(generation.seed())
        );
    }
}
