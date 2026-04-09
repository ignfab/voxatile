package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.utils.axis.mappers.OffsetAxisMapper;
import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.IdentityAxisMapper;

/**
 * An {@link AxisMapperBuilder} for a constant size.
 */
public class ConstantAxisMapperBuilder implements AxisMapperBuilder {
    private final int size;
    private final int origin;
    private boolean adjusted = false;

    /**
     * Creates a new {@code ConstantAxisMapperBuilder}.
     *
     * @param size Size of the built {@link AxisMapper}.
     */
    public ConstantAxisMapperBuilder(int size, int origin) {
        this.size = size;
        this.origin = origin;
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableException {
        if (size != this.size)
            throw new UnbuildableException("Requested size isn't equal to the intrinsic size");
        return adjusted ?
            new OffsetAxisMapper(origin, size) :
            new IdentityAxisMapper(origin, size);
    }

    @Override
    public int maxSizeUnder(int size) {
        return size < this.size ? -1 : this.size;
    }

    @Override
    public int minimumSize() {
        return size;
    }

    @Override
    public int origin() {
        return adjusted ? 0 : origin;
    }

    @Override
    public void makeAdjusted() throws UnbuildableException {
        adjusted = true;
    }
}
