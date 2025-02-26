package com.ignfab.minalac.generator.placeables;

/**
 * A placeable placing nothing.
 * Can be convenient to pass as a no-op voxel type to some operation.
 */
public final class NoVoxel implements Placeable {
    /**
     * NoVoxel singleton instance.
     */
    public static final NoVoxel INSTANCE = new NoVoxel();

    /**
     * NoVoxel private constructor (use INSTANCE instead).
     */
    private NoVoxel() {}

    /**
     * Does not place anything at x, y, z.
     *
     * @param random random generator to use if random needed
     * @param x x-coordinate where to place nothing
     * @param y y-coordinate where to place nothing
     * @param z z-coordinate where to place nothing
     */
    @Override
    public void place(int x, int y, int z) {}
}
