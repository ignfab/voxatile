package com.ignfab.minalac.generator.placeables.work_in_progress.builder;

import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.StretcherIndexMapper;

public class StretcherIndexMapperBuilder implements IndexMapperBuilder {
    IndexMapperBuilder base;
    int stretchableCoord;
    int minSize;
    int maxSize;

    public StretcherIndexMapperBuilder(IndexMapperBuilder base, int stretchableCoord, int minRepetition) {
        this.base = base;
        this.stretchableCoord = stretchableCoord;
        minSize = base.minimumSize() + minRepetition - 1;
        maxSize = Integer.MAX_VALUE;
    }

    public StretcherIndexMapperBuilder(IndexMapperBuilder base, int stretchableCoord, int minRepetition, int maxRepetition) {
        this.base = base;
        this.stretchableCoord = stretchableCoord;
        this.minSize = base.minimumSize() + minRepetition - 1;
        this.maxSize = base.minimumSize() + maxRepetition - 1;
    }

    @Override
    public IndexMapper build(int size) {
        if (ask(size) != size)
            throw new RuntimeException("Not possible");
        return new StretcherIndexMapper(stretchableCoord, base.minimumSize(), size);
    }

    @Override
    public int ask(int size) {
        if (size < minSize)
            return -1;
        return Math.min(maxSize, size);
    }

    @Override
    public int minimumSize() {
        return minSize;
    }

    public static void main(String[] args) {
        IndexMapperBuilder dummy = new DummyIndexMapperBuilder(3, 3);
        IndexMapperBuilder stretcher = new StretcherIndexMapperBuilder(dummy, 1, 1, 2);
        IndexMapper im = stretcher.build(4);

        System.out.println(im.structures());

        for (int c = 0; c < im.size(); c++) {
            System.out.println(c + " -> " + im.placeable(c));
        }
    }
}
