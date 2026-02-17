package com.ignfab.minalac.generator.placeables.work_in_progress.builder;

import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IdentityIndexMapper;

public class SuperDelegateIndexMapperBuilder implements IndexMapperBuilder {
    IndexMapperBuilder[] tab;

    public SuperDelegateIndexMapperBuilder(IndexMapperBuilder[] tab) {
        this.tab = tab;
    }

    @Override
    public IndexMapper build(int size) {
        /*IndexMapper[] mapper = new IndexMapper[tab.length];
        for (int i = 0 ; i < mapper.length; i++)
            mapper[i] = tab[i].build(size);
            */
        return new IdentityIndexMapper(size);
    }

    @Override
    public int ask(int size) {
        for (IndexMapperBuilder builder : tab) {
            if (builder.ask(size) != size)
                return -1;
        }
        return size;
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
