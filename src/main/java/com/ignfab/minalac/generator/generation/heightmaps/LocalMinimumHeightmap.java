package com.ignfab.minalac.generator.generation.heightmaps;

/**
 * Gives the local minimum, around a specified range, at each point of the specified heightmap.
 * The local minimum is calculated at (x ± range; y ± range).
 */
public class LocalMinimumHeightmap extends UnaryOperatorHeightmap {
    private final int range;

    /**
     * Creates a new {@code LocalMinimumHeightmap}.
     *
     * @param base the base heightmap
     * @param range the desired range for local minimum
     */
    public LocalMinimumHeightmap(ReadableHeightmap base, int range) {
        super(base);
        if (range < 0)
            throw new IllegalArgumentException("Range must be a positive integer");
        this.range = range;
    }

    @Override
    public int get(int x, int y) {
        int localMin = base.get(x, y);
        for (int i = x - range; i <= x + range; i++)
            for (int j = y - range; j <= y + range; j++)
                if (bbox().contains(i, j))
                    localMin = Math.min(localMin, base.get(i, j));
        return localMin;
    }
}
