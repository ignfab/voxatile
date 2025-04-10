package com.ignfab.minalac.generator.outputs.testing;

import java.io.File;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldTile;

/**
 * Testing purpose voxel world output.
 *
 * TestingVoxelWorld does not produce any file output. It is intended to be used in unit tests
 * as output world.
 */
public class TestingVoxelWorld extends VoxelWorld {
    private WorldBBox3d maxLimits;

    /**
     * Constructs a new TestingVoxelWorld.
     *
     * @param limits Limits of the voxel world to create
     *
     * Beware: Avoid large limits!
     * Each voxel is stored in memory as a string, which could be very large.
     */
    public TestingVoxelWorld(WorldBBox3d limits) {
        super(null);
        maxLimits = limits;
        if (limits != WorldBBox3d.EMPTY)
            super.setLimits(limits);
    }

    @Override
    public WorldBBox3d maxLimits() {
        return maxLimits;
    }

    // Avoid "Limits already set" when performing deserialization tests
    // and get rid of .setLimits in rendering tests
    @Override
    public void setLimits(WorldBBox3d bbox) {
    }

    @Override
    public void save(File destination) throws MapWriteException {}

    @Override
    public VoxelWorldTile newTile(WorldBBox3d limits) {
        return new TestingVoxelWorldTile(this, limits);
    }
}
