package com.ignfab.minalac.generator.placeables.resized.builders;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;
import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.UnresizableStructureException;
import com.ignfab.minalac.generator.placeables.resized.mappers.StretcherIndexMapper;

public class StretcherIndexMapperBuilder implements IndexMapperBuilder {
    private final IndexMapperBuilder underlying;
    private final int stretchableCoord;
    private final int minSize;
    private final int maxSize;

    public StretcherIndexMapperBuilder(IndexMapperBuilder underlying, int stretchableCoord, int minRepetition) {
        this(underlying, stretchableCoord, minRepetition, Integer.MAX_VALUE);
    }

    public StretcherIndexMapperBuilder(IndexMapperBuilder underlying, int stretchableCoord, int minRepetition, int maxRepetition) {
        this.underlying = underlying;
        this.stretchableCoord = stretchableCoord;
        int underlyingMin = underlying.minimumSize();
        // Voir TODO-12 : le questionnement semble similaire
        if (underlyingMin == 0) {
            minSize = 0;
            maxSize = 0;
        } else {
            minSize = underlyingMin + minRepetition - 1;
            maxSize = (maxRepetition == Integer.MAX_VALUE) ? Integer.MAX_VALUE : underlying.minimumSize() + maxRepetition - 1;
        }
    }

    @Override
    public IndexMapper build(int size) throws UnresizableStructureException {
        if (size < minSize)
            throw new UnresizableStructureException("Not enough space");
        if (size > maxSize)
            throw new UnresizableStructureException("Requested size is too large");
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
