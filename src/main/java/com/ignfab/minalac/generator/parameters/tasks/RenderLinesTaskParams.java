package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.tasks.RenderLinesTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for a {@link RenderLinesTaskParams}.
 */
public class RenderLinesTaskParams extends TileTaskParams {
    /**
     * Models to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    /**
     * What to place (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableStructureParams structure;

    /**
     * If specified, resulting voxels will be placed on this heightmap (optional, default use voxelized altitude).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ReadableHeightmapParams stickToHeightmap;

    /**
     * Render only when above this heightmap (optional, default render always).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ReadableHeightmapParams renderOnlyWhenAbove;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *e
     * @param models selection of models to render
     * @param structure structure to place along lines
     */
    @ConstructorProperties({"models", "structure"})
    public RenderLinesTaskParams(ModelSelectionParams models, PlaceableStructureParams structure) {
        this.models = models;
        this.structure = structure;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        structure.validate();
        models.validate();
        if (renderOnlyWhenAbove != null)
            renderOnlyWhenAbove.validate();
        if (stickToHeightmap != null)
            stickToHeightmap.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        ReadableHeightmapSpec renderOnlyWhenAboveSpec = null;
        ReadableHeightmapSpec stickToHeightmapSpec = null;
        if (renderOnlyWhenAbove != null)
            renderOnlyWhenAboveSpec = renderOnlyWhenAbove.create(generation.heightmaps());
        if (stickToHeightmap != null)
            stickToHeightmapSpec = stickToHeightmap.create(generation.heightmaps());

        PlaceableStructure structure = this.structure.create(generation.seed());
        return new RenderLinesTask(
            models.create(),
            ignored -> structure,
            stickToHeightmapSpec,
            renderOnlyWhenAboveSpec
        );
    }
}

