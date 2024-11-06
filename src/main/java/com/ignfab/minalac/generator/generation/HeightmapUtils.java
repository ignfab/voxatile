package com.ignfab.minalac.generator.generation;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * Utility class containing static methods for performing operations on {@link Heightmap}.
 */
public final class HeightmapUtils {

    private HeightmapUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Applies an average filter of the specified heightmap where values are strictly positive.
     * This method is synchronized preventing potential other threads reading or writing on the heightmap while updating it.
     * Size of the kernel is 3x3.
     *
     * @param heightmap the heightmap on which the partial filter is applied
     */
    public static void applyAverageFilter(Heightmap heightmap) {
        // TODO-PR : Change kernel size? If so average should ignore non zero that are far away
        WorldBBox2d bbox = heightmap.bbox();
        Heightmap buffer = heightmap.copy(1);
        synchronized (heightmap) {
            for (int x = bbox.minX(); x <= bbox.maxX(); x++)
                for (int y = bbox.minY(); y <= bbox.maxY(); y++)
                    if (buffer.get(x, y) > 0)
                        heightmap.set(x, y, average(buffer, x, y, 3));
        }
    }

    private static int average(Heightmap heightmap, int x, int y, int kernelSize) {
        int offset = kernelSize / 2;
        double mean = 0;
        for (int i = x - offset; i <= x + offset; i++)
            for (int j = y - offset; j <= y + offset; j++)
                mean += heightmap.get(i, j);

        return (int) Math.floor(mean / (kernelSize * kernelSize));
    }

    // TODO-PR : Experimentation

    // Can be improved if applied globally
    // if 0 => set(1)
    // (if not0 and not default => min(around) + 1)
    public static void applyManhattan(Heightmap heightmap) {
        WorldBBox2d bbox = heightmap.bbox();
        for (int x = bbox.minX(); x <= bbox.maxX(); x++)
            for (int y = bbox.minY(); y <= bbox.maxY(); y++)
                if (heightmap.get(x, y) > 0)
                    manhattan(heightmap, x, y);
    }

    public static void manhattan(Heightmap heightmap, int x, int y) {
        int k = 0;
        boolean foundZero = false;
        // max k is arbitrary it depends on ReadableHM or WritableHM
        while (!foundZero && k <= 50) {
            k++;
            foundZero = around(heightmap, x, y, k);
        }
        if (foundZero)
            heightmap.set(x, y, k);
    }

    public static boolean around(Heightmap heightmap, int x, int y, int k) {
        int i = -k;
        int x1, y1, x2, y2, x3, y3, x4, y4;

        // Not really a manhattan (diag = 1 and not 2)
        while(i <= k - 1) {
            x1 = i + 1;
            y1 = k;
            x2 = k;
            y2 = i;
            x3 = i;
            y3 = -k;
            x4 = -k;
            y4 = i + 1;
            if (heightmap.get(x + x1, y + y1) == 0 ||
                heightmap.get(x + x2, y + y2) == 0 ||
                heightmap.get(x + x3, y + y3) == 0 ||
                heightmap.get(x + x4, y + y4) == 0)
                return true;
            i++;
        }
        return false;
    }

}
