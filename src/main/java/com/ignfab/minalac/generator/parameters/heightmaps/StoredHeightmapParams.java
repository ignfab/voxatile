package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;

/**
 * Parameter class for retrieving a stored heightmap by its name.
 */
public class StoredHeightmapParams implements ReadableHeightmapParams {
    /**
     * The name of the heightmap.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String stored;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param stored the name of the heightmap.
     */
    @ConstructorProperties("stored")
    public StoredHeightmapParams(String stored) {
        this.stored = stored;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (stored.isBlank())
            throw new IllegalArgumentException("Name cannot be empty or blank");
    }

    @Override
    public Heightmap create(Generation generation) {
        return generation.heightmaps().get(stored);
    }
}
