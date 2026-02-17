package com.ignfab.minalac.generator.placeables.work_in_progress.builder;

import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IdentityIndexMapper;

public class DelegateIndexMapperBuilder implements IndexMapperBuilder {
    IndexMapperBuilder delegatee;

    public DelegateIndexMapperBuilder(IndexMapperBuilder delegatee) {
        this.delegatee = delegatee;
    }

    @Override
    public IndexMapper build(int size) {
        return new IdentityIndexMapper(size);
        //return delegatee.build(size);
    }

    @Override
    public int ask(int size) {
        return delegatee.ask(size);
    }

    @Override
    public int minimumSize() {
        return delegatee.minimumSize();
    }
}
