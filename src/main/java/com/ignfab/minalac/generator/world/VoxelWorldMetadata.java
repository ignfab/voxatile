package com.ignfab.minalac.generator.world;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

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

    public WorldCoords3d getSpawn() {
        return spawn;
    }

    public void setSpawn(WorldCoords3d spawn) {
        this.spawn = spawn;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public WorldBBox3d getBbox() {
        return bbox;
    }

    public void setBbox(WorldBBox3d bbox) {
        this.bbox = bbox;
    }
}
