package com.ignfab.minalac.generator.mcserver;

import java.util.Collection;
import java.util.List;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.VoxelTile;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;

public class MCServerWorld extends VoxelWorld {
    private final String destination;

    private static final WorldBBox3d MAX_LIMITS = new WorldBBox3d(
        new WorldCoords3d(-30_000_000, -30_000_000, -64),
        new WorldCoords3d(30_000_000, 30_000_000, 320)
    );

    public MCServerWorld(String destination) {
        super(new VoxelWorldMetadata());
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public WorldBBox3d maxLimits() {
        return MAX_LIMITS;
    }

    @Override
    public VoxelTile newTile(WorldBBox3d limits) {
        return new MCServerTile(limits);
    }

    @Override
    public void initialize() {}

    @Override
    public void finalizeAndSave() {}

    @Override
    public Collection<WorldBBox2d> tiles(int maxTileSize) {
        return List.of();
    }
}
