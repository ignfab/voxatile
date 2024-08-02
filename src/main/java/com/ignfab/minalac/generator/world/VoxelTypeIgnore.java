package com.ignfab.minalac.generator.world;

/**
 * A voxel type that places no voxel. Can be convenient to pass
 * as a no-op voxel type to some operation.
 */
public class VoxelTypeIgnore implements VoxelType {
    /**
     * Does not place any voxel at x, y, z.
     *
     * @param x x-coordinate where not to place voxel
     * @param y y-coordinate where not to place voxel
     * @param z z-coordinate where not to place voxel
     */
    @Override
    public void place(int x, int y, int z) {}
}
