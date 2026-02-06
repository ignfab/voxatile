package com.ignfab.minalac.generator.outputs.hytale;

import java.util.Collection;

import com.hypixel.hytale.server.core.universe.world.World;

import com.ignfab.minalac.generator.generation.SquareUnitsTileGenerator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.VoxelTile;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;

public class HytaleVoxelWorld extends VoxelWorld {
    public static World world;

    private static final WorldBBox3d MAX_LIMITS = new WorldBBox3d(
        new WorldCoords3d(-30_000_000, -30_000_000, 0),
        new WorldCoords3d(30_000_000, 30_000_000, 319)
    );

    public HytaleVoxelWorld() {
        super(new VoxelWorldMetadata());
    }

    @Override
    public WorldBBox3d maxLimits() {
        return MAX_LIMITS;
    }

    @Override
    public VoxelTile newTile(WorldBBox3d limits) {
        return new HytaleVoxelTile(limits, world);
    }

    @Override
    public void initialize() {}

    @Override
    public void finalizeAndSave() {}

    @Override
    public Collection<WorldBBox2d> tiles(int maxTileSize) {
        SquareUnitsTileGenerator tileGenerator = new SquareUnitsTileGenerator(512, limits().to2d());
        return tileGenerator.getTiles(maxTileSize);
    }
}
