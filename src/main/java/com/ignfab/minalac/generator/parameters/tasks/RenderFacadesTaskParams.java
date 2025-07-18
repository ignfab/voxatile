package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.structures.ElasticPlaceableStructureParams;
import com.ignfab.minalac.generator.tasks.RenderFacadesTask;
import com.ignfab.minalac.generator.tasks.TileTask;


/**
 * Parameters for creating a {@link RenderFacadesTask}.
 */
public class RenderFacadesTaskParams extends TileTaskParams {
    /**
     * Type of models to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    /**
     * Name of the ground heightmap to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams heightmap;

    /**
     * Name of the height metadata to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String heightName;

    /**
     * {@code PlaceableStructure} to use for the ground floor (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ElasticPlaceableStructureParams ground;

    /**
     * {@code PlaceableStructure} to use for the upper floors (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ElasticPlaceableStructureParams floor;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models type of models to render
     * @param heightmap name of the ground heightmap to use
     * @param heightName name of the metadata for facade height
     * @param ground the {@link ElasticPlaceableStructureParams} to use for the ground floor
     * @param floor the {@link ElasticPlaceableStructureParams} to use for the upper floors
     */
    @ConstructorProperties({ "models", "heightmap", "heightName", "ground", "floor"})
    public RenderFacadesTaskParams(
        ModelSelectionParams models,
        ReadableHeightmapParams heightmap,
        String heightName,
        ElasticPlaceableStructureParams ground,
        ElasticPlaceableStructureParams floor
    ) {
        this.models = models;
        this.heightmap = heightmap;
        this.heightName = heightName;
        this.ground = ground;
        this.floor = floor;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        heightmap.validate();
        floor.validate();
        ground.validate();
        models.validate();
        if (heightName.isBlank())
            throw new IllegalArgumentException("heightName can not be blank.");
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderFacadesTask(
            models.create(generation.models()),
            heightmap.create(generation),
            heightName,
            ground.create(generation.seed()),
            floor.create(generation.seed())
        );
    }
}
