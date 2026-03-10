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


        // TODO: Actually we should find minimal size common to all builders.
        // For now, we suppose it is the maximum of all minimal sizes but there could be other solutions.
        // Is finding other solutions complex? To be found out.

        int maxMin = 0;
        for (AxisMapperBuilder builder : builders)
            maxMin = Math.max(maxMin, builder.minimumSize());

        minSize = maxSizeUnder(maxMin);

        if (minSize < 0)
            // Sub-builders can't agree on minimum size
           throw new UnsupportedOperationException("Incompatible builders");
/*

        for (AxisMapperBuilder builder : builders) {
            if (builder.maxSizeUnder(maxMin) != maxMin) {
                System.out.println("Max=%d vs %d".formatted(maxMin, builder.maxSizeUnder(maxMin)));
                // They may be where builders can be compatible
                // Possible size A : 1, 3, 5 B : 2, 4, 6 -> Incompatible
                //  A : 1, 3, 6 B 2, 4, 6 -> Compatible but we would need minOver() and a maximum to limit the search as a param
                throw new UnsupportedOperationException("Incompatible builders");
            }
        }
        this.minSize = maxMin;*/
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableException {
        if (size != maxSizeUnder(size))
            throw new UnbuildableException("Not possible");
        return new IdentityAxisMapper(size);
    }

    @Override
    public int maxSizeUnder(int size) {
        // TODO-16 : can be optimized since there is a min
        if (size < 0)
            return size;

        int minCandidate = size;
        for (AxisMapperBuilder builder : builders)
            minCandidate = Math.min(minCandidate, builder.maxSizeUnder(size));

        if (minCandidate < 0 || minCandidate == size)
            return minCandidate;

        return maxSizeUnder(minCandidate);
    }

    @Override
    public int minimumSize() {
        return minSize;
    }
}
