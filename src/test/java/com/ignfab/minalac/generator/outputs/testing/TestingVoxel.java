package com.ignfab.minalac.generator.outputs.testing;

import java.util.Objects;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A dummy voxel for {@code TestingVoxelWorld} and {@code TestingVoxelTile}.
 * All testing voxels are represented by simple strings.
 */
public class TestingVoxel implements Placeable {
    /**
     * Name of the voxel.
     */
    protected String name;

    /**
     * Creates a new {@code TestingVoxel}.
     *
     * @param name A name for this voxel (this value will be set in voxel world)
     */
    public TestingVoxel(String name) {
        this.name = name;
    }

    /**
     * Places voxel in a {@link VoxelTile}.
     *
     * @param x x-coordinate where to place voxel
     * @param y y-coordinate where to place voxel
     * @param z z-coordinate where to place voxel
     */
    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        ((TestingVoxelTile) tile).set(x, y, z, this);
    }

    /**
     * Returns the name of that voxel.
     *
     * @return Voxel as string
     */
    protected String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestingVoxel that = (TestingVoxel) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
