package com.ignfab.minalac.generator.world;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * A three-dimensional world with voxels as the fundamental unit.
 * Voxels are only accessible through {@link VoxelTile} created by {@code newTile} method.
 * This is an abstract class meant to be extended into specific formats classes (Minecraft, Luanti, ...).
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
        limits = WorldBBox3d.EMPTY;
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
        this.metadata.setSpawn(limits.center());
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
     * Return the maximum limits of the world (hard limit of the format).
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

    /**
     * Creates a new tile for this world (a smaller cubic part).
     * World has to be initialized and not finalized.
     *
     * @param limits Limit of the new tile
     * @return The resulting tile.
     */
    public abstract VoxelTile newTile(WorldBBox3d limits);

    /**
     * Initializes {@code VoxelWorld}.
     * <p>
     * Things that have to be done before working on tiles should be done here.
     * Files and directories may be created here for example.
     * <p>
     * Must be called before saving tiles or finalizing world.
     *
     * @throws MapWriteException if an error occurs while writing to the destination.
     */
    public abstract void initialize() throws MapWriteException;

    /**
     * Finalizes {@code VoxelWorld} so it can be used in game.
     * <p>
     * Things that have to be done after working on tiles should be done here.
     * Files may be created here for example.
     * <p>
     * This method will not actually save map content (voxels),
     * this is done by {@link VoxelTile#save()} which should be called first.
     * <p>
     * Must be called once world is initialized and all tiles saved.
     *
     * @throws MapWriteException if an error occurs while writing to the destination.
     */
    public abstract void finalizeAndSave() throws MapWriteException;
}
