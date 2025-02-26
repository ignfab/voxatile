package com.ignfab.minalac.generator.world;

import java.io.File;

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

    protected VoxelWorld(VoxelWorldMetadata metadata) {
        limits = new WorldBBox3d(0, 0, 0, 0, 0, 0);
        this.metadata = metadata;
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
}
