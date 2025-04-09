package com.ignfab.minalac.generator.world;

import java.util.Collections;
import java.util.Iterator;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * {@code VoxelTile} represents a part of a {@link VoxelWorld} to be generated.
 */
public abstract class VoxelTile {
    /**
     * The limits of the tile.
     */
    private WorldBBox3d limits;
    /**
     * Heightmap representing the altitude of the highest voxels.
     */
    protected Heightmap minimum;
    /**
     * Heightmap representing the altitude of the lowest voxels.
     */
    protected Heightmap maximum;

    /**
     * Creates a new {@code VoxelTile}.
     *
     * @param limits Limits of the tile
     */
    protected VoxelTile(WorldBBox3d limits) {
        this.limits = limits;
        minimum = new Heightmap(limits.to2d(), limits.maxZ());
        maximum = new Heightmap(limits.to2d(), limits.minZ());
    }

    /**
     * Return the limits of this tile.
     *
     * @return the {@code WorldBBox3d} representing the limits of the world
     */
    public WorldBBox3d limits() {
        return limits;
    }

    /**
     * Saves {@code VoxelTile} contents to its final destination so tile could be freed.
     *
     * @throws MapWriteException if an error occurs while writing to destination.
     */
    public abstract void save() throws MapWriteException;

    /**
     * Returns the voxel located at the given coordinates as a new {@code Placeable}.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return the corresponding voxel and {@code null} if the voxel doesn't exist.
     */
    public abstract Placeable getVoxel(int x, int y, int z);

    /**
     * Returns a descending iterator over the voxels and associated coordinates of a column of this world.
     *
     * @param x x-coordinate of the column to iterate over
     * @param y y-coordinate of the column to iterate over
     * @return an iterator for the pair of voxels and coordinates
     */
    public Iterator<PlacedVoxel> voxelIterator(int x, int y) {
        int minZ = minimum.get(x, y);
        int maxZ = maximum.get(x, y);
        if (maxZ < minZ)
            return Collections.emptyIterator();

        return new VoxelColumnIterator(this, x, y, minZ, maxZ);
    }

    /**
     * Returns an iterable over the voxels and associated coordinates of a column of this world.
     *
     * @param x x-coordinate of the column to iterate over
     * @param y y-coordinate of the column to iterate over
     * @return an iterable for the pair of voxels and coordinates
     */
    public Iterable<PlacedVoxel> voxels(int x, int y) {
        return () -> this.voxelIterator(x, y);
    }

    /**
     * Updates the internal minimum or maximum heightmap at the coordinate (x, y) based on the provided z-coordinate value.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     */
    protected void updateHeightmaps(int x, int y, int z) {
        if (z > maximum.get(x, y))
            maximum.set(x, y, z);
        if (z < minimum.get(x, y))
            minimum.set(x, y, z);
    }
}
