package com.ignfab.minalac.generator.placeables.u_turn_wip;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

public class W_StretchIndexMapperBuilder implements IndexMapperBuilder {
    IndexMapperBuilder base;
    int stretchableCoord;
    int minSize;
    int maxSize;

    public W_StretchIndexMapperBuilder(IndexMapperBuilder base, int stretchableCoord, int minRepetition) {
        this.base = base;
        this.stretchableCoord = stretchableCoord;
        minSize = base.minimalSize() + minRepetition - 1;
        maxSize = Integer.MAX_VALUE;
    }

    public W_StretchIndexMapperBuilder(IndexMapperBuilder base, int stretchableCoord, int minRepetition, int maxRepetition) {
        this.base = base;
        this.stretchableCoord = stretchableCoord;
        this.minSize = base.minimalSize() + minRepetition - 1;
        this.maxSize = base.minimalSize() + maxRepetition - 1;
    }

    @Override
    public IndexMapper build(int size) {
        if (ask(size) != size)
            throw new RuntimeException("Not possible");
        return new W_StretchIndexMapper(stretchableCoord, base.minimalSize(), size);
    }

    @Override
    public int ask(int size) {
        if (size < minSize)
            return -1;
        return Math.min(maxSize, size);
    }

    @Override
    public int minimalSize() {
        return minSize;
    }

    public static void main(String[] args) {
        IndexMapperBuilder dummy = new DummyIndexMapperBuilder(3, 3);
        IndexMapperBuilder stretcher = new W_StretchIndexMapperBuilder(dummy, 1, 1, 2);
        IndexMapper im = stretcher.build(4);

        System.out.println(im.structure());

        for (int c = 0; c < im.size(); c++) {
            System.out.println(c + " -> " + im.placeable(c));
        }
    }
}
