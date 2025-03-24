package com.ignfab.minalac.generator.generation.heightmaps;

/**
 * Takes a heightmap and transforms its values to Manhattan distances.
 * The distance is a capped Manhattan distance to the nearest point on the provided heightmap matching the specified {@code targetValue}.
 * The value is capped at {@code maximumDistance}.
 * The provided heightmap remains unchanged and the transformation is done dynamically each time the get method is called.
 */
public class CappedManhattanHeightmap extends UnaryOperatorHeightmap {
    private final int maximumDistance;
    private final int targetValue;

    /**
     * Creates a new {@link CappedManhattanHeightmap}.
     *
     * @param base the base heightmap
     * @param maximumDistance the maximum allowed distance
     * @param targetValue the value used for distance calculations
     */
    public CappedManhattanHeightmap(ReadableHeightmap base, int maximumDistance, int targetValue) {
        super(base);
        this.maximumDistance = maximumDistance;
        this.targetValue = targetValue;
    }

    @Override
    public int get(int x, int y) {
        if (isTargetValue(x, y))
            return 0;
        for (int distance = 1; distance < maximumDistance; distance++) {
            for (int i = 0; i < distance; i++)
                if (isTargetValue(x + i, y + distance - i)
                    || isTargetValue(x - i + distance, y - i)
                    || isTargetValue(x - i, y - distance + i)
                    || isTargetValue(x - distance + i, y + i))
                    return distance;
        }
        return maximumDistance;
    }

    private boolean isTargetValue(int x, int y) {
        return bbox().contains(x, y) && base.get(x, y) == targetValue;
    }
}
