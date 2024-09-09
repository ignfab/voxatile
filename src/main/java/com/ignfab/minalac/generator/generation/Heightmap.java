package com.ignfab.minalac.generator.generation;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.WorldSize2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.ArrayChunk2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.IterableChunk2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dIterator;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dIteratorAll;

/**
 * A 2d height map in voxel world units.
 */
public class Heightmap extends ArrayChunk2d implements IterableChunk2d {

    /**
     * Constructs a new Heightmap.
     *
     * @param originX X-coordinate of origin point
     * @param originY Y-coordinate of origin point
     * @param sizeX Size of height map along X-axis
     * @param sizeY Size of height map along Y-axis
     * @param defaultValue Default value for all height map cells
     */
    public Heightmap(int originX, int originY, int sizeX, int sizeY, int defaultValue) {
        super(originX, originY, sizeX, sizeY, defaultValue);
    }

    /**
     * Constructs a new Heightmap.
     *
     * @param box Bounding box of height map
     * @param defaultValue Default value for all height map cells
     */
    public Heightmap(WorldBBox2d box, int defaultValue) {
        super(box, defaultValue);
    }

    /**
     * Constructs a new Heightmap.
     *
     * @param origin Origin point of height map
     * @param size Size of height map
     * @param defaultValue Default value for all height map cells
     */
    public Heightmap(WorldCoords2d origin, WorldSize2d size, int defaultValue) {
        super(origin, size, defaultValue);
    }

    /**
     * Returns a default iterator for a height map.
     *
     * @return A {@code Chunk2dIterator} iterating over all elements of the height map
     */
    @Override
    public Chunk2dIterator iterator() {
        return new Chunk2dIteratorAll(this);
    }
}
