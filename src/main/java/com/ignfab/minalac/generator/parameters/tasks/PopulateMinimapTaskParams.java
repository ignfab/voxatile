package com.ignfab.minalac.generator.parameters.tasks;

import java.awt.Color;
import java.beans.ConstructorProperties;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.tasks.PopulateMinimapTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for {@link PopulateMinimapTask}.
 */
public class PopulateMinimapTaskParams extends TaskParams {
    /**
     * Name of the minimap to populate (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String minimap;

    /**
     * Mapping of voxel identifiers to their display colors (required).
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    public Map<String, Color> colors;

    /**
     * Constructor used to ensure that the required fields are present during
     * deserialization.
     *
     * @param minimap name of the minimap to populate
     * @param colors mapping of voxel identifiers to their display colors
     */
    @ConstructorProperties({"minimap", "colors"})
    public PopulateMinimapTaskParams(String minimap, Map<String, Color> colors) {
        this.minimap = minimap;
        this.colors = colors;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (minimap.isBlank())
            throw new IllegalArgumentException("Minimap name cannot be empty or blank");
    }

    @Override
    public TileTask create(Generation generation) {
        return new PopulateMinimapTask(generation.minimaps().get(minimap), colors);
    }
}
