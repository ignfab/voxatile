package com.ignfab.minalac.generator.placeables.resized.builders;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;
import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.mappers.IdentityIndexMapper;

public class ConstantIndexMapperBuilder implements IndexMapperBuilder {
    private final int theOnlyAllowedLength;

    public ConstantIndexMapperBuilder(int theOnlyAllowedLength) {
        this.theOnlyAllowedLength = theOnlyAllowedLength;
    }

    @Override
    public IndexMapper build(int size) {
        // TODO Probleme avec ce test
        if (size != theOnlyAllowedLength)
            throw new RuntimeException("Identity : Not possible");
        return new IdentityIndexMapper(size);
    }

    @Override
    public int maxSizeUnder(int size) {
        if (size < theOnlyAllowedLength)
            return -1;
        return theOnlyAllowedLength;
    }

    @Override
    public int minimumSize() {
        return theOnlyAllowedLength;
    }
}
