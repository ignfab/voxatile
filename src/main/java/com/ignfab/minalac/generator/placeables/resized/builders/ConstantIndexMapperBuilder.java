package com.ignfab.minalac.generator.placeables.resized.builders;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;
import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.UnresizableStructureException;
import com.ignfab.minalac.generator.placeables.resized.mappers.IdentityIndexMapper;

public class ConstantIndexMapperBuilder implements IndexMapperBuilder {
    private final int theOnlyAllowedLength;

    public ConstantIndexMapperBuilder(int theOnlyAllowedLength) {
        this.theOnlyAllowedLength = theOnlyAllowedLength;
    }

    @Override
    public IndexMapper build(int size) throws UnresizableStructureException {
        if (size != theOnlyAllowedLength)
            throw new UnresizableStructureException("Requested size isn't equal to the intrinsic size");
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
