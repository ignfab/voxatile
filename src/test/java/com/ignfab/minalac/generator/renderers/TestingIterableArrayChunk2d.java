package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.utils.world2d.chunk.ArrayChunk2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.IterableChunk2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dIteratorAll;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

public class TestingIterableArrayChunk2d extends ArrayChunk2d implements IterableChunk2d {
    public TestingIterableArrayChunk2d(WorldBBox2d bbox, int defaultValue) {
        super(bbox, defaultValue);
    }

    public Chunk2dIteratorAll iterator() {
        return new Chunk2dIteratorAll(this);
    }
}
