package com.ignfab.minalac.generator.placeables.resized.builders;

import java.util.Arrays;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;
import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.mappers.IdentityIndexMapper;

public class SuperDelegateIndexMapperBuilder implements IndexMapperBuilder {
    private final IndexMapperBuilder[] builders;
    private final int minSize;

    public SuperDelegateIndexMapperBuilder(IndexMapperBuilder[] builders) {
        this.builders = builders;
        // TODO-14 : Il y a d'autre comptabilité mais c'est le plus bourrin
        int maxMin = 0;
        for (IndexMapperBuilder builder : builders)
            maxMin = Math.max(maxMin, builder.minimumSize());

        for (IndexMapperBuilder builder : builders) {
            if (builder.maxSizeUnder(maxMin) != maxMin)
                throw new RuntimeException("Incompatible builders");
        }
        this.minSize = maxMin;
    }

    @Override
    public IndexMapper build(int size) {
        /*IndexMapper[] mapper = new IndexMapper[tab.length];
        for (int i = 0 ; i < mapper.length; i++)
            mapper[i] = tab[i].build(size);
            */
        return new IdentityIndexMapper(size);
    }

    @Override
    public int maxSizeUnder(int size) {
        /*
        int minCandidate = Integer.MAX_VALUE;
        for (IndexMapperBuilder builder : builders) {
            int currentMax = builder.maxSizeUnder(size);
            minCandidate = Math.min(minCandidate, currentMax);
            if (currentMax != size)
                return -1;
        }
        return size;
         */
        return compute(size);
    }

    // TODO-15 : Ca me semble OK mais vérifier
    private int compute(int size) {
        if (size < 0)
            return size;
        boolean disagree = false;
        int minCandidate = size;
        for (IndexMapperBuilder builder : builders) {
            int currentPossibleValue = builder.maxSizeUnder(size);
            minCandidate = Math.min(minCandidate, currentPossibleValue);
            if (currentPossibleValue != size)
                disagree = true;
        }
        if (disagree)
            return compute(minCandidate);
        return minCandidate;
    }

    @Override
    public int minimumSize() {
        return minSize;
    }
}
