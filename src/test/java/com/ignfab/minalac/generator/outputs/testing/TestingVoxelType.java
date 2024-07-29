package com.ignfab.minalac.generator.outputs.testing;

import com.ignfab.minalac.generator.world.OutOfWorldException;
import com.ignfab.minalac.generator.world.VoxelType;

/**
 * A dummy voxelType implementation for {@code TestingVoxelWorld}.
 * All testing voxel type are represented by simple strings.
 */
public class TestingVoxelType implements VoxelType {
    /**
     * World where voxel are placed.
     */
    protected TestingVoxelWorld world;

    /**
     * Type of the voxel type.
     */
    protected String type;

    /**
     * Creates a new TestingVoxelType.
     *
     * @param world World in which this voxel type will be placed
     * @param type A string for this voxel type (this value will be set in voxel world)
     */
    protected TestingVoxelType(TestingVoxelWorld world, String type) {
        this.world = world;
        this.type = type;
    }

    /**
     * Places a voxel of this type in the world.
     *
     * @param x x-coordinate where to place voxel
     * @param y y-coordinate where to place voxel
     * @param z z-coordinate where to place voxel
     */
    @Override
    public void place(int x, int y, int z) throws OutOfWorldException {
        this.world.set(x, y, z, this);
    }

    /**
     * Returns the string type of that voxel type.
     *
     * @return Voxel type as string
     */
    protected String getType() {
        return type;
    }
}
