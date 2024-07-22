package com.ignfab.minalac.generator.generation;

import com.ignfab.minalac.generator.models.Voxelizable3d;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.WorldSize2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.SimpleVoxelizer3d;
import com.ignfab.minalac.generator.voxelization.Voxel3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer3d;

import java.util.Arrays;

/**
 * A 2d heightmap in voxel world units.
 */
public class Heightmap implements Voxelizable3d {
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
        values = new int[bbox.getSize().x() * bbox.getSize().y()];
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
        return (x - bbox.getMin().x()) * bbox.getSize().y() + (y - bbox.getMin().y());
    }

    /**
     * Returns the bounding box of the heightmap.
     *
     * @return the {@link WorldBBox2d} associated to the chunk.
     */
    public WorldBBox2d bbox() {
        return bbox;
    }

    /**
     * Returns the height at coordinates.
     *
     * @param x the x-coordinate value.
     * @param y the y-coordinate value.
     * @return the {@code int} height at the coordinate (x, y).
     * @throws IndexOutOfBoundsException if provided coordinates are outside the chunk.
     */
    public int get(int x, int y) {
        return values[index(x, y)];
    }

    /**
     * Returns the height at coordinates.
     *
     * @param coords the coordinates.
     * @return the {@code int} height at the provided coordinates.
     * @throws IndexOutOfBoundsException if provided coordinates are outside the chunk.
     */
    public int get(WorldCoords2d coords) {
        return get(coords.x(), coords.y());
    }

    /**
     * Set the height at the specified coordinates.
     *
     * @param x the x-coordinate value.
     * @param y the y-coordinate value.
     * @param height the height to set.
     * @throws IndexOutOfBoundsException if provided coordinates are outside the chunk.
     */
    public void set(int x, int y, int height) {
        values[index(x, y)] = height;
    }

    /**
     * Set the height at the specified coordinates.
     *
     * @param coords the coordinates.
     * @param height the height to set.
     * @throws IndexOutOfBoundsException if provided coordinates are outside the chunk.
     */
    public void set(WorldCoords2d coords, int height) {
        set(coords.x(), coords.y(), height);
    }

    /**
     * Returns a voxelizer for this heightmap.
     * The voxelizer will iterate over all (x, y, height) coordinates
     * of this heightmap that are also contained in the given bounding box.
     *
     * @param bbox the limits for this voxelization.
     * @return a {@link Voxelizer3d} iterating over all elements of the heightmap.
     */
    @Override
    public Voxelizer3d voxelize3d(WorldBBox3d bbox) {
        WorldBBox2d intersection = this.bbox.intersection(bbox.to2d());
        return new SimpleVoxelizer3d(() -> new RemapIterator<>(intersection, coords -> new Voxel3d.Impl(coords.to3d(get(coords)))), bbox);
    }
}
