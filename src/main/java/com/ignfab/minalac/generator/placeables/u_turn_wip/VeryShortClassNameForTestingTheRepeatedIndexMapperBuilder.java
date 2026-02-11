package com.ignfab.minalac.generator.placeables.u_turn_wip;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

public class VeryShortClassNameForTestingTheRepeatedIndexMapperBuilder implements IndexMapperBuilder {
    IndexMapperBuilder base;
    int minSize;
    int maxSize;

    public VeryShortClassNameForTestingTheRepeatedIndexMapperBuilder(IndexMapperBuilder base, int minOccur) {
        this.base = base;
        this.minSize = base.minimalSize() * minOccur;
        this.maxSize = Integer.MAX_VALUE;
    }

    @Override
    public IndexMapper build(int size) {
        if (size < minSize)
            throw new RuntimeException("Not possible");

        int r = size % minSize;
        int n = size / minSize;
        int[] lengths = new int[4];
        // TODO DEMAIN :
        // Demande à enfant si possible d'ajouter
        // Dans la mesure du possible faire une repartition equitable
        return new LengthIndexMapper(0);
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int ask(int size) {
        if (size == 0 || size < minSize) return 0;
        int r = size % minSize;
        for (int i = 0; i <= r; i ++) {
            if (base.ask(minSize + i) == minSize + i)
                return size;
        }
        return 0;
    }

    @Override
    public int minimalSize() {
        return minSize;
    }
}
