package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.WritableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.tasks.PopulateHeightmapTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link PopulateHeightmapTask}.
 */
public class PopulateHeightmapTaskParams extends TileTaskParams {
    /**
     * The type of models to get data from (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;
    /**
     * The name of the heightmap to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public WritableHeightmapParams heightmap;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models models selection to get data from.
     * @param heightmap the name of the heightmap to use.
     */
    @ConstructorProperties({"models", "heightmap"})
    public PopulateHeightmapTaskParams(ModelSelectionParams models, WritableHeightmapParams heightmap) {
        this.models = models;
        this.heightmap = heightmap;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        heightmap.validate();
        models.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new PopulateHeightmapTask(
            models.create(generation.models()),
            heightmap.create(generation.heightmaps())
        );
    }
}
