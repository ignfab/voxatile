package com.ignfab.minalac.generator.parameters;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.minimaps.Minimap;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * Params for {@link Minimap}.
 */
public class MinimapParams {

    /**
     * Maximum size of the minimap in pixels (both width and height).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int size = 1000;

    /**
     * Checks if there are any blatantly invalid parameters.
     *
     * @throws IllegalArgumentException is any of the parameters is invalid.
     */
    public void validate() throws IllegalArgumentException {
        if (size <= 0)
            throw new IllegalArgumentException("'size' must be greater than zero");
    }

    /**
     * Create the {@link Minimap}.
     *
     * @param worldLimits limits of the world
     * @return {@link Minimap} instance
     */
    public Minimap create(WorldBBox2d worldLimits) {
        return new Minimap(worldLimits, size);
    }
}
