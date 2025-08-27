package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A placeable placing nothing.
 * Can be convenient to pass this to some operation when not wanting place anything.
 */
public final class Nothing implements Pattern {
    /**
     * Nothing singleton instance.
     */
    public static final Nothing INSTANCE = new Nothing();

    /**
     * Nothing private constructor (use INSTANCE instead).
     */
    private Nothing() {}

    /**
     * Does not place anything at x, y, z.
     *
     * @param x x-coordinate where to place nothing
     * @param y y-coordinate where to place nothing
     * @param z z-coordinate where to place nothing
     */
    @Override
    public void place(VoxelTile tile, int x, int y, int z) {}

    @Override
    public Placeable get(int x, int y, int z) {
        return this;
    }
}
