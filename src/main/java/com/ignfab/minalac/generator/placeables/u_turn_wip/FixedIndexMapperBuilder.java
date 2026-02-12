package com.ignfab.minalac.generator.placeables.u_turn_wip;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

public class FixedIndexMapperBuilder implements IndexMapperBuilder {
    int theOnlyAllowedLength;

    public FixedIndexMapperBuilder(int theOnlyAllowedLength) {
        this.theOnlyAllowedLength = theOnlyAllowedLength;
    }

    @Override
    public IndexMapper build(int size) {
        if (size != theOnlyAllowedLength)
            throw new RuntimeException("Not possible");
        return new SameSameIndexMapper(size);
    }

    @Override
    public int ask(int size) {
        if (size < theOnlyAllowedLength)
            return 0;
        return theOnlyAllowedLength;
    }

    @Override
    public int minimumSize() {
        return theOnlyAllowedLength;
    }
}
