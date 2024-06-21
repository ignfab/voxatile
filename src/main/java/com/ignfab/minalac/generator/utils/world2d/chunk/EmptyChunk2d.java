package com.ignfab.minalac.generator.utils.world2d.chunk;

import java.util.Collections;
import java.util.Iterator;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dElement;

/**
 * A 2d chunk of size 0.
 */
public final class EmptyChunk2d implements IterableChunk2d, WritableChunk2d {
    private static final EmptyChunk2d INSTANCE = new EmptyChunk2d();
    private static final WorldBBox2d BBOX = new WorldBBox2d(0, 0, 0, 0);

    private EmptyChunk2d() {}

    public static EmptyChunk2d getInstance() {
        return INSTANCE;
    }

    @Override
    public WorldBBox2d bbox() {
        return BBOX;
    }

    @Override
    public int get(int x, int y) {
        throw new IndexOutOfBoundsException(String.format("%s: Index out of range at (x=%d, y=%d)", getClass().getSimpleName(), x, y));
    }

    @Override
    public int get(WorldCoords2d pos) {
        throw new IndexOutOfBoundsException(String.format("%s: Index out of range at (x=%d, y=%d)", getClass().getSimpleName(), pos.x(), pos.y()));
    }

    @Override
    public void set(int x, int y, int value) {
        throw new IndexOutOfBoundsException(String.format("%s: Index out of range at (x=%d, y=%d)", getClass().getSimpleName(), x, y));
    }

    @Override
    public void set(WorldCoords2d pos, int value) {
        throw new IndexOutOfBoundsException(String.format("%s: Index out of range at (x=%d, y=%d)", getClass().getSimpleName(), pos.x(), pos.y()));
    }

    @Override
    public Iterator<Chunk2dElement> iterator() {
        return Collections.emptyIterator();
    }

}
