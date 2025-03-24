package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A readable heightmap that always returns the specified value.
 */
public class ConstantHeightmap implements ReadableHeightmap {
    private final int value;
    private final WorldBBox2d bbox;

    /**
     * Creates a new {@code ConstantHeightmap}.
     *
     * @param value the constant value
     * @param bbox the bounding box of the heightmap
     */
    public ConstantHeightmap(int value, WorldBBox2d bbox) {
        this.value = value;
        this.bbox = bbox;
    }

    @Override
    public int get(int x, int y) {
        if (!bbox.contains(x, y))
            throw new IndexOutOfBoundsException("Index out of range at (x=%d, y=%d)".formatted(x, y));
        return value;
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }
}
