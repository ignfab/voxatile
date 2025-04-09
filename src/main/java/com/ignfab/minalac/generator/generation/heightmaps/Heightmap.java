package com.ignfab.minalac.generator.generation.heightmaps;

import java.util.Arrays;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.WorldSize2d;

/**
 * A 2d heightmap in voxel world units.
 */
public class Heightmap implements ReadableHeightmap {
    private final WorldBBox2d bbox;
    private int[] values;
    /**
     * A reusable instance of an empty {@link Heightmap}.
     */
    public static final Heightmap EMPTY = new Heightmap(WorldBBox2d.EMPTY, 0);

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
     * @param bbox Bounding box of heightmap
     * @param defaultValue Default value for all heightmap cells
     */
    public Heightmap(WorldBBox2d bbox, int defaultValue) {
        this.bbox = bbox;
        values = new int[bbox.sizeX() * bbox.sizeY()];
        if (defaultValue != 0)
            Arrays.fill(values, defaultValue);
    }

    /**
     * Creates a new {@link Heightmap}.
     *
     * @param origin Origin point of heightmap
     * @param size Size of heightmap
     * @param defaultValue Default value for all heightmap cells
     */
    public Heightmap(WorldCoords2d origin, WorldSize2d size, int defaultValue) {
        this(new WorldBBox2d(origin, size), defaultValue);
    }

    private Heightmap(WorldBBox2d bbox, int[] values) {
        if (values.length != bbox.size().area())
            throw new IllegalArgumentException("Provided array length doesn't match bbox area");
        this.bbox = bbox;
        this.values = Arrays.copyOf(values, values.length);
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

    /**
     * Sets the height at a specified position.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @param height the height to set at specified position
     * @throws IndexOutOfBoundsException if position is outside the heightmap.
     */
    public void set(int x, int y, int height) {
        values[index(x, y)] = height;
    }

    /**
     * Sets the height at a specified position.
     *
     * @param position the position
     * @param height the height to set at specified position
     * @throws IndexOutOfBoundsException if position is outside the heightmap.
     */
    public void set(WorldCoords2d position, int height) {
        set(position.x(), position.y(), height);
    }

    /**
     * Returns a copy of this heightmap.
     *
     * @return a copy of this heightmap.
     */
    public Heightmap copy() {
        return new Heightmap(bbox, values);
    }

    /**
     * Replaces all height values of this heightmap with the heights of the provided heightmap.
     * The provided heightmap must match this heightmap bounding box.
     * The other heightmap will get the current values of this heightmap.
     *
     * @param other the heightmap
     */
    public void swap(Heightmap other) {
        if (!other.bbox.equals(bbox))
            throw new IllegalArgumentException("Bounding boxes are not equal");
        int[] values = this.values;
        this.values = other.values;
        other.values = values;
    }
}
