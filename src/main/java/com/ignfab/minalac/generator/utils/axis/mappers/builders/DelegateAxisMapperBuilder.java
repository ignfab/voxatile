package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.utils.axis.mappers.IdentityAxisMapper;
import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;

/**
 * An {@link AxisMapperBuilder} inheriting everything from another {@link AxisMapperBuilder}.
 */
public class DelegateAxisMapperBuilder implements AxisMapperBuilder {
    AxisMapperBuilder delegatee;

    public DelegateAxisMapperBuilder(AxisMapperBuilder delegatee) {
        this.delegatee = delegatee;
    }

    // TODO-17 : Le throws!
    @Override
    public AxisMapper build(int size) throws UnbuildableException {
        if (delegatee.maxSizeUnder(size) != size)
            throw new UnbuildableException("?");
        return new IdentityAxisMapper(size);
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
