package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.StretcherIndexMapper;

/**
 * Builder for an {@link AxisMapper} that stretches an interval at a given coordinate.
 */
public class StretcherAxisMapperBuilder implements AxisMapperBuilder {
    // For now, the only AxisMapperBuilder that is passed is the ConstantAxisMapperBuilder.
    // StretcherIndexMapper was functionally made to work with something constant.
    // If something else is passed, the resulting mapping might be incorrect.
    // The AxisMapperBuilder is kept for consistency.
    private final AxisMapperBuilder underlying;
    private final int stretchableCoord;
    private final int minSize;
    private final int maxSize;

    /**
     * Creates a new {@code StretcherAxisMapperBuilder}.
     *
     * @param underlying {@link AxisMapperBuilder} to stretch
     * @param stretchableCoord coordinate where to stretch {@code underlying}
     * @param minRepetition minimum possible repetitions of stretchable coordinate
     * @param maxRepetition maximum possible repetitions of stretchable coordinate
     * @throws UnbuildableException if underlying builder is not adjustable
     */
    public StretcherAxisMapperBuilder(AxisMapperBuilder underlying, int stretchableCoord, int minRepetition, int maxRepetition) throws UnbuildableException {
        // If changed, RepeatAxisMapperBuilder should have same behavior
        if (underlying.minimumSize() <= 0)
            throw new UnbuildableException("Underlying has a minimum size of zero. Can not be stretched");
        this.underlying = underlying;
        this.stretchableCoord = stretchableCoord;
        int underlyingMin = underlying.minimumSize();
        minSize = underlyingMin + minRepetition - 1;
        maxSize = (maxRepetition == Integer.MAX_VALUE) ? Integer.MAX_VALUE : underlying.minimumSize() + maxRepetition - 1;
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableException {
        if (size < minSize)
            throw new UnbuildableException("Not enough space");
        if (size > maxSize)
            throw new UnbuildableException("Requested size is too large");
        return new StretcherIndexMapper(underlying.origin(), stretchableCoord, underlying.minimumSize(), size);
    }

    @Override
    public int maxSizeUnder(int size) {
        if (size < minSize)
            return -1;
        return Math.min(size, maxSize);
    }

    @Override
    public int minimumSize() {
        return minSize;
    }

    @Override
    public int origin() {
        return underlying.origin();
    }
}
