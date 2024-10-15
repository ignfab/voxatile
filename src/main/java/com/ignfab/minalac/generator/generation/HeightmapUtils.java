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

}
