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
 * Parameters for a {@link RenderDynamicLinesTaskParams}.
 */
public class RenderDynamicLinesTaskParams extends TileTaskParams {
    /**
     * Models to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams width;

    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableStructureParams repeated;

    @JsonSetter(nulls = Nulls.SKIP)
    public PlaceableStructureParams extra;

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

    @ConstructorProperties({"models", "width", "repeated"})
    public RenderDynamicLinesTaskParams(ModelSelectionParams models, ModelValueParams width, PlaceableStructureParams repeated) {
        this.models = models;
        this.width = width;
        this.repeated = repeated;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (stickToHeightmap != null && stickToAltitude != null)
            throw new IllegalArgumentException("Can not use both stickToHeightmap and stickToAltitude fields");
        models.validate();
        width.validate();
        repeated.validate();
        if (extra != null)
            extra.validate();
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

        RenderLinesTask.GetZ stickToZ;
        if (stickToHeightmap != null) {
            ReadableHeightmapSpec stickToHeightmapSpec = stickToHeightmap.create(generation.heightmaps());
            stickToZ = (tile, model, coords) -> tile.heightmaps().get(stickToHeightmapSpec).get(coords);
        } else {
            ModelValue stickToAltitudeValue = stickToAltitude.create(generation);
            stickToZ = (tile, model, coords) -> stickToAltitudeValue.getAsInt(model).orElseThrow(() -> new IgnorableException("Missing altitude value"));
        }

        PlaceableStructure repeatedStructure = repeated.create(generation.seed());
        if (repeatedStructure.limits().sizeY() != 1)
            throw new IllegalArgumentException("repeated must have width == 1");
        PlaceableStructure extraStructure = extra == null ? null : extra.create(generation.seed());
        if (extraStructure != null && (repeatedStructure.limits().sizeX() != extraStructure.limits().sizeX() || repeatedStructure.limits().sizeZ() != extraStructure.limits().sizeZ()))
            throw new IllegalArgumentException("repeated and extra must have same length & height");
        ModelValue widthValue = width.create(generation);
        return new RenderLinesTask(
            models.create(),
            model -> {
                int width = widthValue.getAsInt(model).orElseThrow(() -> new IgnorableException("Missing line width"));
                // Meh! structure.merge(otherStructure, offset)?
                PlaceableStructure structure = new PlaceableStructure();
                for (int w = 0; w < width; w++)
                    for (int x = 0; x < repeatedStructure.limits().sizeX(); x++)
                        for (int z = 0; z < repeatedStructure.limits().sizeZ(); z++)
                            structure.set(x, w, z, repeatedStructure.get(x, 0, z));
                if (extraStructure != null)
                    for (int x = 0; x < extraStructure.limits().sizeX(); x++)
                        for (int y = 0; y < extraStructure.limits().sizeY(); y++)
                            for (int z = 0; z < extraStructure.limits().sizeZ(); z++)
                                structure.set(x, width + y, z, extraStructure.get(x, y, z));
                return structure;
            },
            stickToZ,
            renderOnlyWhenAboveSpec
        );
    }
}
