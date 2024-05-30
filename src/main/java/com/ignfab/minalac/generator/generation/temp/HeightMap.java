package com.ignfab.minalac.generator.generation.temp;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.WorldSize2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.ArrayChunk2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.IterableChunk2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dIterator;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dIteratorAll;

//This class will probably be added on an upcoming pull-request (since it doesn't belong to the package utils.world2d.chunk)
public class HeightMap extends ArrayChunk2d implements IterableChunk2d {
    public HeightMap(int originX, int originY, int sizeX, int sizeY, int defaultValue) {
        super(originX, originY, sizeX, sizeY, defaultValue);
    }

    public HeightMap(WorldBBox2d box, int defaultValue) {
        super(box, defaultValue);
    }

    public HeightMap(WorldCoords2d coords, WorldSize2d size, int defaultValue) {
        super(coords, size, defaultValue);
    }

    @Override
    public Chunk2dIterator iterator() {
        return new Chunk2dIteratorAll(this);
    }
}
