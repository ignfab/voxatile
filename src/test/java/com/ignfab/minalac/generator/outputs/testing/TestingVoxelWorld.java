package com.ignfab.minalac.generator.outputs.testing;

import java.util.Collection;
import java.util.Collections;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelTile;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;

/**
 * Testing purpose {@link VoxelWorld} intended to be used in unit tests.
 */
public class TestingVoxelWorld extends VoxelWorld {
    private static final WorldBBox3d MAX_LIMIT = new WorldBBox3d(
        new WorldCoords3d(-100, -100, -100),
        new WorldCoords3d(100, 100, 100)
    );

    /**
     * A default {@code TestingVoxelWorld} instance to pass as argument when it won't actually be used.
     */
    public static final TestingVoxelWorld UNUSED = new TestingVoxelWorld();

    /**
     * Creates a new TestingVoxelWorld.
     */
    public TestingVoxelWorld() {
        super(new VoxelWorldMetadata());
    }

    @Override
    public void initialize() throws MapWriteException {}

    @Override
    public void finalizeAndSave() throws MapWriteException {}

    /**
     * {@inheritDoc}
     *
     * Beware: Avoid large limits!
     * Each voxel is stored in memory as a string, which could be very large.
     */
    @Override
    public VoxelTile newTile(WorldBBox3d limits) {
        return new TestingVoxelTile(this, limits);
    }

    @Override
    public WorldBBox3d maxLimits() {
        return MAX_LIMIT;
    }

    @Override
    public Collection<WorldBBox2d> tiles(int maxTileSize) {
        return Collections.singleton(maxLimits().to2d());
    }
}
