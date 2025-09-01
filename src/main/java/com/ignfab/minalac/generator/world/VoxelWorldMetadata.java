package com.ignfab.minalac.generator.world;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * The {@code VoxelWorldMetadata} class contains the different metadata for a {@link VoxelWorld}.
 */
public class VoxelWorldMetadata {
    /**
     * Initial position of the player.
     */
    protected WorldCoords3d spawn;
    /**
     * Name of the generated world.
     */
    protected String worldName;

    /**
     * {@return the initial spawn position of the player}
     */
    public WorldCoords3d getSpawn() {
        return spawn;
    }

    /**
     * Sets the initial spawn position of the player.
     *
     * @param spawn the initial spawn position of the player
     */
    public void setSpawn(WorldCoords3d spawn) {
        this.spawn = spawn;
    }

    /**
     * {@return the name of the world}
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Sets the name of the world.
     *
     * @param worldName the name of the world
     */
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
}
