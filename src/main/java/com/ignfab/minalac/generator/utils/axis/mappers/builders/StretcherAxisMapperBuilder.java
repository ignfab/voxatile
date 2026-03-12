package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.StretcherIndexMapper;

/**
 * Builder for an {@link AxisMapper} that stretches an interval.
 */
public class StretcherAxisMapperBuilder implements AxisMapperBuilder {
    private final AxisMapperBuilder underlying;
    private final int stretchableCoord;
    private final int minSize;
    private final int maxSize;

    public StretcherAxisMapperBuilder(AxisMapperBuilder underlying, int stretchableCoord, int minRepetition, int maxRepetition) {
        this.underlying = underlying;
        this.stretchableCoord = stretchableCoord;
        int underlyingMin = underlying.minimumSize();
        // Voir TODO-12 : le questionnement semble similaire
        if (underlyingMin == 0) {
            // Baldy implemented -> underlying can have a min of 0 and not being "empty"
            // Could be corrected with minSizeOver(0)
            minSize = 0;
            maxSize = 0;
        } else {
            minSize = underlyingMin + minRepetition - 1;
            maxSize = (maxRepetition == Integer.MAX_VALUE) ? Integer.MAX_VALUE : underlying.minimumSize() + maxRepetition - 1;
        }
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableException {
        if (size < minSize)
            throw new UnbuildableException("Not enough space");
        if (size > maxSize)
            throw new UnbuildableException("Requested size is too large");
        return new StretcherIndexMapper(stretchableCoord, underlying.minimumSize(), size);
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
}
