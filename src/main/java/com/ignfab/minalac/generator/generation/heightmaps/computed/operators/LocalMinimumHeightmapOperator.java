package com.ignfab.minalac.generator.generation.heightmaps.computed.operators;

import com.ignfab.minalac.generator.generation.heightmaps.AreaNeeds;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * Gives the local minimum, around a specified range, at each point of the specified heightmap.
 * The local minimum is calculated at (x ± range; y ± range).
 */
public class LocalMinimumHeightmapOperator implements UnaryHeightmapOperator {
    private final int range;

    /**
     * Creates a new {@code LocalMinimumHeightmapOperator}.
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
                localMin = Math.min(localMin, operand.get(i, j));
        return localMin;
    }

    @Override
    public AreaNeeds neededAreas(WorldBBox2d area, ReadableHeightmapSpec operandSpec) {
        return operandSpec.neededAreas(area).enlarged(new WorldBBox2d(-range, -range, range * 2 + 1, range * 2 +1));
    }
}
