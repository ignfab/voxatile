package com.ignfab.minalac.generator.generation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.ignfab.minalac.generator.utils.IntegerInterval;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A tile generator for worlds with square map units.
 * <p>
 * It will create tiles:
 * <ul>
 *   <li>No larger than the given size;</li>
 *   <li>Covering the whole given area;</li>
 *   <li>Not sharing any map unit;</li>
 * </ul>
 * <p>
 * Map units are minimal parts of the world that shouldn't be generated separately
 * (i.e. map blocks or regions depending on output format terminology).
 * In this tile generator, map units are fixed size squares, aligned at (0, 0).
 * <p>
 * It is suitable for both Luanti and Minecraft output formats.
 */
public class SquareUnitsTileGenerator {

    private final int unitSize;
    private final WorldBBox2d bbox;

    /**
     * Creates a new {@code SquareUnitsTileGenerator} instance.
     *
     * @param unitSize size in voxel of square map units edges
     * @param bbox box to be tiled
     */
    public SquareUnitsTileGenerator(int unitSize, WorldBBox2d bbox) {
        this.unitSize = unitSize;
        this.bbox = bbox;
    }

    // Computes smallest coordinate of map unit containing pos.
    private int unitFloor(int pos) {
        return pos - Math.floorMod(pos, unitSize);
    }

    // Computes fairly sized slices smaller than maxSize, covering min to max (inclusive).
    private List<IntegerInterval> slices(int min, int max, int maxSize) {
        if (maxSize < unitSize)
            throw new IllegalArgumentException("maxSize should be larger than unitSize");

        List<Integer> sizes = new LinkedList<>();

        // Basic silly slicing
        int pos = min;
        while (pos + maxSize < max) {
            int newpos = unitFloor(pos + maxSize);
            sizes.add(newpos - pos - 1);
            pos = newpos;
        }
        sizes.add(max - pos);

        // Adjust slice sizes to distribute fairly
        while (true) {
            int smaller = Collections.min(sizes);
            int larger = Collections.max(sizes);

            if (larger < smaller + unitSize + 1)
                break;

            int amount = Math.max(unitSize, unitFloor((larger - smaller) / 2));

            sizes.set(sizes.indexOf(smaller), smaller + amount);
            sizes.set(sizes.indexOf(larger), larger - amount);
        }

        // Build result as IntegerIntervals
        pos = min;
        List<IntegerInterval> result = new LinkedList<>();
        for (int size : sizes) {
            result.add(new IntegerInterval(pos, pos + size));
            pos += size + 1;
        }
        return result;
    }

    /**
     * Creates tiles respecting given maximum tile size.
     *
     * @param maxTileSize maximum tile size (on x and y axes)
     *
     * @return a list of boxes representing resulting tiles
     */
    public List<WorldBBox2d> getTiles(int maxTileSize) {

        // Impossible case
        if (maxTileSize < unitSize)
            throw new IllegalArgumentException("Max tile size must be at least " + unitSize);

        // Obvious case
        if (maxTileSize > bbox.sizeX() && maxTileSize > bbox.sizeY())
            return Collections.singletonList(bbox);

        List<WorldBBox2d> result = new LinkedList<>();

        List<IntegerInterval> xs = slices(bbox.minX(), bbox.maxX(), maxTileSize);
        List<IntegerInterval> ys = slices(bbox.minY(), bbox.maxY(), maxTileSize);

        for (IntegerInterval y : ys)
            for (IntegerInterval x : xs)
                result.add(new WorldBBox2d(x.begin(), y.begin(), x.size(), y.size()));

        return result;
    }
}
