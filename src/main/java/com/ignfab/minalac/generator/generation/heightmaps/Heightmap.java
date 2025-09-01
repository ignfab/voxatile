package com.ignfab.minalac.generator.generation.heightmaps;

import java.util.Arrays;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A 2d heightmap in voxel world units.
 */
public class Heightmap implements WritableHeightmap {
    private WorldBBox2d bbox; // Bbox of values array
    private final int defaultValue;
    private int[] values; // Will be created at first write attempt

    /**
     * Creates a new {@link Heightmap}.
     *
     * @param originX X-coordinate of origin point
     * @param originY Y-coordinate of origin point
     * @param sizeX Size of heightmap along X-axis
     * @param sizeY Size of heightmap along Y-axis
     * @param defaultValue Default value for all heightmap cells
     */
    public Heightmap(int originX, int originY, int sizeX, int sizeY, int defaultValue) {
        this(new WorldBBox2d(originX, originY, sizeX, sizeY), defaultValue);
    }

    /**
     * Creates a new {@link Heightmap}.
     *
     * @param bbox Bounding box of the heightmap
     * @param defaultValue Default value for all heightmap cells
     */
    public Heightmap(WorldBBox2d bbox, int defaultValue) {
        this.bbox = bbox;
        this.defaultValue = defaultValue;
        values = null;
    }

    /**
     * Creates a new {@link Heightmap} that is a copy of given {@link ReadableHeightmap}.
     *
     * @param other Heightmap to copy
     */
    public Heightmap(ReadableHeightmap other) {
        this.bbox = other.bbox();
        this.defaultValue = 0; // TODO: Something better ? ReadableHeightmap may not have default value
        values = new int[bbox.size().area()];
        copyValues(other);
    }

    @Override
    public void includeArea(WorldBBox2d area) {
        if (bbox.contains(area))
            return;

        if (values != null)
            throw new IllegalStateException("Can't change an already populated heightmap size");

        bbox = WorldBBox2d.surrounding(bbox, area);
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public int get(int x, int y) {
        if (values == null || !bbox.contains(x, y))
            return defaultValue;

        return values[x - bbox.minX() + bbox.sizeX() * (y - bbox.minY())];
    }

    @Override
    public void set(int x, int y, int height) {
        if (!bbox.contains(x, y))
            throw new IndexOutOfBoundsException("Index out of range at (x=%d, y=%d)".formatted(x, y));

        if (values == null) {
            values = new int[bbox.size().area()];
            if (defaultValue != 0)
                Arrays.fill(values, defaultValue);
        }

        values[x - bbox.minX() + bbox.sizeX() * (y - bbox.minY())] = height;
    }

    @Override
    public void copyValues(ReadableHeightmap other) {
        if (values == null)
            values = new int[bbox.size().area()];

        if (other instanceof Heightmap hm && hm.values != null && other.bbox().equals(bbox)) {
            // If same bbox and same class, we can go faster
            System.arraycopy(hm.values, 0, values, 0, values.length);
            return;
        }

        WorldBBox2d intersection = other.bbox().intersection(bbox);
        for (WorldCoords2d position : intersection)
            set(position, other.get(position));
    }

    @Override
    public Heightmap copy() {
        return new Heightmap(this);
    }
}
