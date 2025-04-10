package com.ignfab.minalac.generator.generation.heightmaps;

import java.util.Arrays;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A 2d heightmap in voxel world units.
 */
public class Heightmap implements WritableHeightmap {
    private final WorldBBox2d bbox;
    private final int[] values;

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
        values = new int[bbox.size().area()];
        if (defaultValue != 0)
            Arrays.fill(values, defaultValue);
    }

    /**
     * Creates a new {@link Heightmap} that is a copy of given {@link ReadableHeightmap}.
     *
     * @param other Heightmap to copy
     */
    public Heightmap(ReadableHeightmap other) {
        this.bbox = other.bbox();
        values = new int[bbox.size().area()];
        copyValues(other);
    }

    private int index(int x, int y) {
        if (!bbox.contains(x, y))
            throw new IndexOutOfBoundsException("Index out of range at (x=%d, y=%d)".formatted(x, y));
        return (x - bbox.minX()) * bbox.sizeY() + (y - bbox.minY());
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public int get(int x, int y) {
        return values[index(x, y)];
    }

    @Override
    public void set(int x, int y, int height) {
        values[index(x, y)] = height;
    }

    @Override
    public void copyValues(ReadableHeightmap other) {
        if (other instanceof Heightmap hm && other.bbox().equals(bbox)) {
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
