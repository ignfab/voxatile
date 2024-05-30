package com.ignfab.minalac.generator.world;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

public class VoxelWorldMetadata {
    protected WorldCoords3d spawn;
    protected String worldName;
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
