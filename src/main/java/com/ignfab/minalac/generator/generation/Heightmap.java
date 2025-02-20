package com.ignfab.minalac.generator.generation;

import java.util.Arrays;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.WorldSize2d;

/**
 * A 2d heightmap in voxel world units.
 */
public class Heightmap {
    /**
     * The bounding box of the heightmap.
     */
    protected final WorldBBox2d bbox;
    /**
     * The array used to store the height values.
     */
    protected final int[] values;

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

    private int index(int x, int y) {
        if (!bbox.contains(x, y))
            throw new IndexOutOfBoundsException("Index out of range at (x=%d, y=%d)".formatted(x, y));
        return (x - bbox.minX()) * bbox.sizeY() + (y - bbox.minY());
    }

    /**
     * Returns the bounding box of the heightmap.
     *
     * @return the {@link WorldBBox2d} associated to the heightmap.
     */
    public WorldBBox2d bbox() {
        return bbox;
    }

    /**
     * Returns the height at a specified position.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return the {@code int} height at specified position
     * @throws IndexOutOfBoundsException if position is outside the heightmap.
     */
    public int get(int x, int y) {
        return values[index(x, y)];
    }

    /**
     * Returns the height at a specified position.
     *
     * @param position the position
     * @return the {@code int} height at specified position
     * @throws IndexOutOfBoundsException if position is outside the heightmap.
     */
    public int get(WorldCoords2d position) {
        return get(position.x(), position.y());
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
}
