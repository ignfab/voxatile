package com.ignfab.minalac.generator.placeables.resized.builders;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;
import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.mappers.IdentityIndexMapper;

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
