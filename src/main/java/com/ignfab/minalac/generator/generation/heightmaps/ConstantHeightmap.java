package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A readable heightmap that always returns the specified value.
 *
 * This heightmaps could be used without being bound (it's both bound and unbound).
 * Anywayn the whole mechanism will bind it.
 */
public class ConstantHeightmap implements ReadableHeightmap, UnboundReadableHeightmap {
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
    public WorldBBox2d bbox() {
        return WorldBBox2d.INFINITE;
    }

    @Override
    public ReadableHeightmap bind(GenerationTile tile) {
        return this;
    }
}
