package com.ignfab.minalac.generator.placeables.u_turn_wip;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

public class DummyIndexMapperBuilder implements IndexMapperBuilder {
    int min;
    int max;

    public DummyIndexMapperBuilder(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public IndexMapper build(int size) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int ask(int size) {
        if (max <= size)
            return max;
        if (min <= size)
            return size;
        return 0;
    }

    public static void main(String[] args) {
        IndexMapperBuilder b = new DummyIndexMapperBuilder(4, 6);
        for(int i = 0; i < 9; i ++)
            System.out.println(i + " -> "+ b.ask(i));
    }

    @Override
    public int minimalSize() {
        return min;
    }
}
