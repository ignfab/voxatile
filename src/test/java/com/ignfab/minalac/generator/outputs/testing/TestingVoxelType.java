package com.ignfab.minalac.generator.outputs.testing;

import com.ignfab.minalac.generator.placeables.VoxelType;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * A dummy voxelType implementation for {@code TestingVoxelWorld}.
 * All testing voxel type are represented by simple strings.
 */
public class TestingVoxelType implements VoxelType {
    /**
     * Type of the voxel type.
     */
    protected String type;

    /**
     * Creates a new TestingVoxelType.
     *
     * @param type A string for this voxel type (this value will be set in voxel world)
     */
    public TestingVoxelType(String type) {
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
    public void place(VoxelWorld world, int x, int y, int z) {
        ((TestingVoxelWorld) world).set(x, y, z, this);
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
