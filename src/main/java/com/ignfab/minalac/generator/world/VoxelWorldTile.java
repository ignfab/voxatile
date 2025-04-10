package com.ignfab.minalac.generator.world;

import java.io.File;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * {@code VoxelWorldTile} represents a part of a {@link VoxelWorld} to be generated.
 */
public abstract class VoxelWorldTile {
    /**
     * The limits of the tile.
     */
    private WorldBBox3d limits;

    /**
     * Creates a new {@code VoxelWorldTile}.
     *
     * @param world {@link VoxelWorld} which tile belongs to
     * @param limits Limits of the tile
     */
    protected VoxelWorldTile(VoxelWorld world, WorldBBox3d limits) {
        if (!world.limits().contains(limits))
            throw new IllegalArgumentException("Tile out of world limits");
        this.limits = limits;
    }

    /**
     * Return the limits of this tile.
     *
     * @return the {@code WorldBBox3d} representing the limits of the world
     */
    public WorldBBox3d limits() {
        return limits;
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
