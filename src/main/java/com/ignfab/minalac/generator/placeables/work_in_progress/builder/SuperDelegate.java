package com.ignfab.minalac.generator.placeables.work_in_progress.builder;

import java.util.Arrays;

import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.CompositeIndexMapper;

public class SuperDelegate implements IndexMapperBuilder {
    IndexMapperBuilder[] tab;

    @Override
    public CompositeIndexMapper build(int size) {
        IndexMapper[] mapper = new IndexMapper[tab.length];
        for (int i = 0 ; i < mapper.length; i++)
            mapper[i] = tab[i].build(size);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int ask(int size) {
        int min = tab[0].ask(size);
        for (int i = 1; i < tab.length; i++) {
            if (min > tab[i].ask(size)) {
                min = tab[i].ask(size);
            }
        }
        return min;
    }

    @Override
    public int minimumSize() {
        int min = tab[0].minimumSize();
        for (int i = 1; i < tab.length; i++) {
            if (min > tab[i].minimumSize()) {
                min = tab[i].minimumSize();
            }
        }
        return min;
    }
}
