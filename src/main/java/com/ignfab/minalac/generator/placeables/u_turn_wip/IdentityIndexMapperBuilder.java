package com.ignfab.minalac.generator.placeables.u_turn_wip;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

public class IdentityIndexMapperBuilder implements IndexMapperBuilder {
    int theOnlyAllowedLength;

    public IdentityIndexMapperBuilder(int theOnlyAllowedLength) {
        this.theOnlyAllowedLength = theOnlyAllowedLength;
    }

    @Override
    public IndexMapper build(int size) {
        // TODO Probleme avec ce test
        // if (size != theOnlyAllowedLength)
        //    throw new RuntimeException("Identity : Not possible");
        return new IdentityIndexMapper(size);
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
