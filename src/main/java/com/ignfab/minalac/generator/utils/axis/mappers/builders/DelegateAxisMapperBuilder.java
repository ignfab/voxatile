package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.IdentityAxisMapper;

/**
 * An {@link AxisMapperBuilder} inheriting everything from another {@link AxisMapperBuilder}.
 */
public class DelegateAxisMapperBuilder implements AxisMapperBuilder {
    private final AxisMapperBuilder delegatee;
    private boolean adjusted = false;

    /**
     * Creates a new {@code DelegateAxisMapperBuilder} for a given {@link AxisMapperBuilder}.
     *
     * @param delegatee an {@link AxisMapperBuilder} from which everything in inherited
     */
    public DelegateAxisMapperBuilder(AxisMapperBuilder delegatee) {
        this.delegatee = delegatee;
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableException {
        if (adjusted ? (delegatee.maxSizeUnder(size) != size) : (delegatee.maxSizeUnder(size) > size))
            throw new UnbuildableException("Builder could not fit size=%d (Builder is %s)".formatted(size, delegatee));
        return new IdentityAxisMapper(delegatee.origin(), size);
    }

    @Override
    public int maxSizeUnder(int size) {
        return delegatee.maxSizeUnder(size);
    }

    @Override
    public int minimumSize() {
        return delegatee.minimumSize();
    }

    @Override
    public int origin() {
        return delegatee.origin();
    }

    @Override
    public void makeAdjusted() throws UnbuildableException {
        delegatee.makeAdjusted();
        adjusted = true;
    }
}
