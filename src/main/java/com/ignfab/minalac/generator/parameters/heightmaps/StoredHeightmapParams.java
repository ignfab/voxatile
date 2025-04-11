package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Store;
import com.ignfab.minalac.generator.generation.heightmaps.UnboundHeightmap;


/**
 * Parameter class for retrieving a stored heightmap by its name.
 */
public class StoredHeightmapParams implements ReadableHeightmapParams {
    /**
     * The name of the heightmap.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String name;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param name the name of the heightmap.
     */
    @ConstructorProperties("name")
    public StoredHeightmapParams(String name) {
        this.name = name;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty or blank");
    }

    @Override
    public UnboundHeightmap create(Store<UnboundHeightmap> store) {
        return store.get(name);
    }
}
