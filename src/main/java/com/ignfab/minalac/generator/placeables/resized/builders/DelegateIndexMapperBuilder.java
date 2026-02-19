package com.ignfab.minalac.generator.placeables.resized.builders;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;
import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.UnresizableStructureException;
import com.ignfab.minalac.generator.placeables.resized.mappers.IdentityIndexMapper;

public class DelegateIndexMapperBuilder implements IndexMapperBuilder {
    IndexMapperBuilder delegatee;

    public DelegateIndexMapperBuilder(IndexMapperBuilder delegatee) {
        this.delegatee = delegatee;
    }

    // TODO-17 : Le throws!
    @Override
    public IndexMapper build(int size) throws UnresizableStructureException {
        if (delegatee.maxSizeUnder(size) != size)
            throw new UnresizableStructureException("?");
        return new IdentityIndexMapper(size);
        //return delegatee.build(size);
    }

    @Override
    public int maxSizeUnder(int size) {
        return delegatee.maxSizeUnder(size);
    }

    @Override
    public int minimumSize() {
        return delegatee.minimumSize();
    }
}
