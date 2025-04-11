package com.ignfab.minalac.generator.generation.heightmaps;

/**
 * Gives the local minimum, around a specified range, at each point of the specified heightmap.
 * The local minimum is calculated at (x ± range; y ± range).
 */
public class LocalMinimumHeightmapOperator implements UnaryHeightmapOperator {
    private final int range;

    /**
     * Creates a new {@code LocalMinimumHeightmap}.
     *
     * @param range the desired range for local minimum
     */
    public LocalMinimumHeightmapOperator(int range) {
        if (range < 0)
            throw new IllegalArgumentException("Range must be a positive integer");
        this.range = range;
    }

    @Override
    public int compute(int x, int y, ReadableHeightmap operand) {
        int localMin = operand.get(x, y);
        for (int i = x - range; i <= x + range; i++)
            for (int j = y - range; j <= y + range; j++)
                if (operand.bbox().contains(i, j))
                    localMin = Math.min(localMin, operand.get(i, j));
        return localMin;
    }
}
