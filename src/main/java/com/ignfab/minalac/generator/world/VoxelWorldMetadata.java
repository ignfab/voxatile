package com.ignfab.minalac.generator.world;

import org.geotools.api.geometry.BoundingBox3D;
import org.geotools.geometry.jts.ReferencedEnvelope3D;

public class VoxelWorldMetadata {
    protected int spawnX, spawnY, spawnZ; // TODO Replace by WorldCoords3d
    protected String worldName;
    protected BoundingBox3D bbox; // TODO Replace by WorldBbox3d

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public int getSpawnZ() {
        return spawnZ;
    }

    public void setSpawn(int spawnX, int spawnY, int spawnZ) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public BoundingBox3D getBbox() {
        return bbox;
    }

    public void setBbox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.bbox = new ReferencedEnvelope3D(minX, maxX, minY, maxY, minZ, maxZ, null);
    }
}
