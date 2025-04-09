package com.ignfab.minalac.generator.world;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.placeables.VoxelType;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * The {@code VoxelWorld} abstract class represents a three-dimensional world with voxels as the fundamental unit.
 * Implementations of this abstract class are primary meant to create playable world for voxel-based games such as Minecraft or Minetest.
 *
 * @see com.ignfab.minalac.generator.placeables.VoxelType
 */
public abstract class VoxelWorld {
    /**
     * The limits of the world.
     */
    private WorldBBox3d limits;
    /**
     * The metadata of the world.
     */
    protected final VoxelWorldMetadata metadata;
    /**
     * Heightmap representing the altitude of the highest voxels.
     */
    protected Heightmap minimum;
    /**
     * Heightmap representing the altitude of the lowest voxels.
     */
    protected Heightmap maximum;

    protected VoxelWorld(VoxelWorldMetadata metadata) {
        limits = WorldBBox3d.EMPTY;
        this.metadata = metadata;
        minimum = Heightmap.EMPTY;
        maximum = Heightmap.EMPTY;
    }

    /**
     * Sets the limits of this world.
     * This operation can be only be done once.
     *
     * @param limits a bounding box representing the limits of the world
     * @throws IllegalStateException if the limits have already been set
     * @throws IllegalArgumentException if the specified exceed the maximum limits
     */
    public void setLimits(WorldBBox3d limits) {
        if (!maxLimits().contains(limits))
            throw new IllegalArgumentException("Provided limits exceed the maximum limits");
        if (!this.limits.isEmpty())
            throw new IllegalStateException("The limits have already been set");
        this.limits = limits;
        minimum = new Heightmap(limits().to2d(), limits().maxZ());
        maximum = new Heightmap(limits().to2d(), limits().minZ());
    }

    /**
     * Return the limits of this world.
     *
     * @return the {@code WorldBBox3d} representing the limits of the world
     */
    public WorldBBox3d limits() {
        return limits;
    }

    /**
     * Return the maximum limits of the world.
     *
     * @return the {@code WorldBBox3d} representing the maximum limits of the world
     */
    public abstract WorldBBox3d maxLimits();

    /**
     * Returns the metadata of this {@code VoxelWorld}.
     *
     * @return the {@link VoxelWorldMetadata}
     */
    public VoxelWorldMetadata getMetadata() {
        return metadata;
    }

    // TODO: There is an inconsistency between the two implementations. When the folder does not exist MTVoxelWorld creates it whereas MCVoxelWorld throws a MapWriteException.
    /**
     * Exports the {@code VoxelWorld} and saves it in the specified destination.
     *
     * @param destination the {@link File} where the output will be saved. It must represent a directory.
     * @throws MapWriteException if an error occurs while writing to the specified destination.
     */
    public abstract void save(File destination) throws MapWriteException;

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

    /**
     * Returns the voxel located at the given coordinates as a new {@code VoxelType}.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return the corresponding voxel and {@code null} if the voxel doesn't exist.
     */
    public abstract VoxelType getVoxel(int x, int y, int z);

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
}
