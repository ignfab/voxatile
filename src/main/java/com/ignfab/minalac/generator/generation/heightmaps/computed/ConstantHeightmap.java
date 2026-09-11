package com.ignfab.minalac.generator.generation.heightmaps.computed;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapStore;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;

/**
 * A readable heightmap that always returns the specified value.
 */
public class ConstantHeightmap extends ReadableHeightmapSpec implements ReadableHeightmap {
    private final int value;

    /**
     * Creates a new {@code ConstantHeightmap}.
     *
     * @param value the constant value
     */
    public ConstantHeightmap(int value) {
        this.value = value;
    }

    @Override
    public int get(int x, int y) {
        return value;
    }

    @Override
    protected ReadableHeightmap create(HeightmapStore store) {
        // This heightmap is its own spec (its instance will always be the same regardless of the context).
        return this;
    }
}
