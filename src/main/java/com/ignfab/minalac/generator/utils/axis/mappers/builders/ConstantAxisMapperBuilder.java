package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.utils.axis.mappers.IdentityAxisMapper;
import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;

/**
 * An {@link AxisMapperBuilder} for a constant size.
 */
public class ConstantAxisMapperBuilder implements AxisMapperBuilder {
    private final int size;

    /**
     * Creates a new {@code ConstantAxisMapperBuilder}.
     *
     * @param size Size of the built {@link AxisMapper}.
     */
    public ConstantAxisMapperBuilder(int size) {
        this.size = size;
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableException {
        if (size != this.size)
            throw new UnbuildableException("Requested size isn't equal to the intrinsic size");
        return new IdentityAxisMapper(size);
    }

    @Override
    public int maxSizeUnder(int size) {
        return size < this.size ? -1 : this.size;
    }

    @Override
    public int minimumSize() {
        return size;
    }
}
