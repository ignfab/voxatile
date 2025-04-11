package com.ignfab.minalac.generator.generation.heightmaps;

/**
 * Takes a heightmap and transforms its values to Manhattan distances.
 * The distance is a capped Manhattan distance to the nearest point on the provided heightmap matching the specified {@code targetValue}.
 * The value is capped at {@code maximumDistance}.
 * The provided heightmap remains unchanged and the transformation is done dynamically each time the get method is called.
 */
public class CappedManhattanHeightmapOperator implements UnaryHeightmapOperator {
    private final int maximumDistance;
    private final int targetValue;

    /**
     * Creates a new {@link CappedManhattanHeightmap}. TODO
     *
     * @param maximumDistance the maximum allowed distance
     * @param targetValue the value used for distance calculations
     */
    public CappedManhattanHeightmapOperator(int maximumDistance, int targetValue) {
        this.maximumDistance = maximumDistance;
        this.targetValue = targetValue;
    }

    @Override
    public int compute(int x, int y, ReadableHeightmap operand) {
        if (isTargetValue(x, y, operand))
            return 0;
        for (int distance = 1; distance < maximumDistance; distance++) {
            for (int i = 0; i < distance; i++)
                if (isTargetValue(x + i, y + distance - i, operand)
                    || isTargetValue(x - i + distance, y - i, operand)
                    || isTargetValue(x - i, y - distance + i, operand)
                    || isTargetValue(x - distance + i, y + i, operand))
                    return distance;
        }
        return maximumDistance;
    }

    private boolean isTargetValue(int x, int y, ReadableHeightmap operand) {
        return operand.bbox().contains(x, y) && operand.get(x, y) == targetValue;
    }
}

