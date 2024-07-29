package com.ignfab.minalac.generator.world;

import java.io.File;

/**
 * The {@code VoxelWorld} interface represents a three-dimensional world with voxels as the fundamental unit.
 * Implementations of this interface are primary meant to create playable world for voxel-based games such as Minecraft or Minetest.
 *
 * @see VoxelType
 */
public interface VoxelWorld {
    /**
     * Returns the factory for creating its corresponding {@link VoxelType}.
     *
     * @return the factory for creating voxels
     */
    VoxelTypeFactory getFactory();

    /**
     * Returns the metadata of this {@code VoxelWorld}.
     *
     * @return the {@link VoxelWorldMetadata}
     */
    VoxelWorldMetadata getMetadata();

    // TODO: There is an inconsistency between the two implementations. When the folder does not exist MTVoxelWorld creates it whereas MCVoxelWorld throws a MapWriteException.
    /**
     * Exports the {@code VoxelWorld} and saves it in the specified destination.
     *
     * @param destination the {@link File} where the output will be saved. It must represent a directory.
     * @throws MapWriteException if an error occurs while writing to the specified destination.
     */
    void save(File destination) throws MapWriteException;
}
