package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.models.values.ModelValueParams;
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
     * Cannot be specified along with stickToAltitude.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ReadableHeightmapParams stickToHeightmap;

    /**
     * If specified, resulting voxels will be placed at this altitude (optional, default use voxelized altitude).
     * Cannot be specified along with stickToHeightmap.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ModelValueParams stickToAltitude;

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
        if (stickToHeightmap != null && stickToAltitude != null)
            throw new IllegalArgumentException("Can not use both stickToHeightmap and stickToAltitude fields");
        structure.validate();
        models.validate();
        if (renderOnlyWhenAbove != null)
            renderOnlyWhenAbove.validate();
        if (stickToHeightmap != null)
            stickToHeightmap.validate();
        if (stickToAltitude != null)
            stickToAltitude.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        ReadableHeightmapSpec renderOnlyWhenAboveSpec = null;
        if (renderOnlyWhenAbove != null)
            renderOnlyWhenAboveSpec = renderOnlyWhenAbove.create(generation.heightmaps());

        RenderLinesTask.GetZ stickToZ = null;
        if (stickToHeightmap != null) {
            ReadableHeightmapSpec stickToHeightmapSpec = stickToHeightmap.create(generation.heightmaps());
            stickToZ = (tile, model, coords) -> tile.heightmaps().get(stickToHeightmapSpec).get(coords);
        }
        if (stickToAltitude != null) {
            ModelValue stickToAltitudeValue = stickToAltitude.create(generation);
            stickToZ = (tile, model, coords) -> stickToAltitudeValue.getAsInt(model).orElseThrow(() -> new IgnorableException("Missing altitude value"));
        }

        PlaceableStructure structure = this.structure.create(generation.seed());
        return new RenderLinesTask(
            models.create(),
            ignored -> structure,
            stickToZ,
            renderOnlyWhenAboveSpec
        );
    }
}

