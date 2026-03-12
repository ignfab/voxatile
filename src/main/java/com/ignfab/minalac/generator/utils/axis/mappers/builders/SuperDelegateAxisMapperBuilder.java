package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.utils.axis.mappers.IdentityAxisMapper;
import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;

/**
 *
 */
public class SuperDelegateAxisMapperBuilder implements AxisMapperBuilder {
    private final AxisMapperBuilder[] builders;
    private final int minSize;

    public SuperDelegateAxisMapperBuilder(AxisMapperBuilder... builders) {
        this.builders = builders;
        // TODO-14 : Il y a d'autre comptabilité mais c'est le plus bourrin
        int maxMin = 0;
        for (AxisMapperBuilder builder : builders)
            maxMin = Math.max(maxMin, builder.minimumSize());

        for (AxisMapperBuilder builder : builders) {
            if (builder.maxSizeUnder(maxMin) != maxMin) {
                // They may be where builders can be compatible
                // Possible size A : 1, 3, 5 B : 2, 4, 6 -> Incompatible
                //  A : 1, 3, 6 B 2, 4, 6 -> Compatible but we would need minOver() and a maximum to limit the search as a param
                throw new UnsupportedOperationException("Incompatible builders");
            }
        }
        this.minSize = maxMin;
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableException {
        if (size != maxSizeUnder(size))
            throw new UnbuildableException("Not possible");
        return new IdentityAxisMapper(size);
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
        // TODO-16 : can be optimized since there is a min
        if (size < 0)
            return size;
        boolean disagree = false;
        int minCandidate = size;
        for (AxisMapperBuilder builder : builders) {
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
