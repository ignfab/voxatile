package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.tasks.RenderLines2dTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for {@link RenderLines2dTask}.
 */
public class RenderLines2dTaskParams extends ModelTaskParams {
    /**
     * Structure to place and repeat along lines (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableStructureParams structure;

    /**
     * Heightmap on which draw lines (from which Z is taken according to X and Y).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams heightmap;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param structure structure to place along lines
     * @param heightmap heightmap on which draw lines (from which Z is taken according to X and Y)
     */
    @ConstructorProperties({"structure", "heightmap"})
    public RenderLines2dTaskParams(PlaceableStructureParams structure, ReadableHeightmapParams heightmap) {
        this.structure = structure;
        this.heightmap = heightmap;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        structure.validate();
        models.validate();
        heightmap.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderLines2dTask(
            models.create(),
            structure.create(generation.seed()),
            heightmap.create(generation.heightmaps())
        );
    }
}

