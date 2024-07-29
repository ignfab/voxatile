package com.ignfab.minalac.generator.world;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
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
     * Bounding box of the generated area.
     * Can be used in some output format to restrict player's movements to the generated area only.
     */
    protected WorldBBox3d bbox;

    /**
     * Returns the initial position of the player.
     *
     * @return the spawn of the player
     */
    public WorldCoords3d getSpawn() {
        return spawn;
    }

    /**
     * Sets the initial position of the player.
     *
     * @param spawn the initial position of the player
     */
    public void setSpawn(WorldCoords3d spawn) {
        this.spawn = spawn;
    }

    /**
     * Returns the name of the world.
     *
     * @return the name of the world
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

    /**
     * Returns the {@link WorldBBox3d} of the world.
     *
     * @return the {@link WorldBBox3d} of the world
     */
    public WorldBBox3d getBbox() {
        return bbox;
    }

    /**
     * Sets the {@link WorldBBox3d} of the world.
     *
     * @param bbox the {@link WorldBBox3d} of the world
     */
    public void setBbox(WorldBBox3d bbox) {
        this.bbox = bbox;
    }
}
