package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * Abstract class for all readable heightmaps that are the result of an operation on another {@code ReadableHeightmap}.
 */
public abstract class UnaryOperatorHeightmap implements ReadableHeightmap {
    /**
     * The base heightmap.
     */
    protected final ReadableHeightmap base;

    protected UnaryOperatorHeightmap(ReadableHeightmap base) {
        this.base = base;
    }

    @Override
    public WorldBBox2d bbox() {
        return base.bbox();
    }
}
