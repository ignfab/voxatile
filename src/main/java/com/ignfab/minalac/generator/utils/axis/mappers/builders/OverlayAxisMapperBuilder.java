package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.utils.axis.mappers.IdentityAxisMapper;
import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;

/**
 * An {@link AxisMapperBuilder} made of multiple overlayed {@link AxisMapperBuilder}s.
 */
public class OverlayAxisMapperBuilder implements AxisMapperBuilder {
    private final AxisMapperBuilder[] builders;
    private int minimumSize = 0;
    private int origin = 0;
    private boolean adjusted = false;

    public OverlayAxisMapperBuilder(AxisMapperBuilder... builders) {
        this.builders = builders;
        if (builders.length > 0) {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;

            for (AxisMapperBuilder builder : builders) {
                min = Math.min(min, builder.origin());
                max = Math.max(max, builder.origin() + builder.minimumSize());
            }

            origin = min;
            minimumSize = max - min;
        }
    }

    @Override
    public AxisMapper build() {
        return new IdentityAxisMapper(origin, minimumSize);
    };

    @Override
    public AxisMapper build(int size) throws UnbuildableException {
        if (size < 0)
            throw new IllegalArgumentException("Size must be positive or zero");

        int possible = maxSizeUnder(size);
        if (size != possible)
            throw new UnbuildableException("Impossible to build for this size (%d, possible %d)".formatted(size, possible));

        return new IdentityAxisMapper(origin, size);
    }

    private int adjustedMaxSizeUnder(int size) {
        if (size < 0)
            return size;

        int minCandidate = size;
        for (AxisMapperBuilder builder : builders)
            minCandidate = Math.min(minCandidate, builder.maxSizeUnder(size));

        if (minCandidate < 0 || minCandidate == size)
            return minCandidate;

        return adjustedMaxSizeUnder(minCandidate);
    }

    @Override
    public int maxSizeUnder(int size) {
        if (adjusted)
            return adjustedMaxSizeUnder(size);
        else
            return size < minimumSize ? -1 : size;
    }

    @Override
    public int minimumSize() {
        return minimumSize;
    }

    @Override
    public int origin() {
        return origin;
    }

    public void makeAdjusted() throws UnbuildableException {
        if (builders.length > 0) {
            int size = 0;

            // TODO-14 : Il y a d'autre comptabilité mais c'est le plus bourrin
            // TODO: Actually we should find minimal size common to all builders.
            // For now, we suppose it is the maximum of all minimal sizes but there could be other solutions.
            // Is finding other solutions complex? To be found out.

            for (AxisMapperBuilder builder: builders) {
                builder.makeAdjusted();
                size = Math.max(size, builder.minimumSize());
            }
            // We are looking for the lower common possible size of underlying builders.
            // TODO: Do better!
            // Here, we only give one try, this is very simplistic.
            // minimum size is of course at least the max of all minimum sizes
            // but it could be a greated size (or it could not exist).
            size = adjustedMaxSizeUnder(size);

            if (size < 0)
                throw new UnbuildableException("Unable to adjust dimensions (builders don't agree)");

            minimumSize = size;
        }
        origin = 0;
        adjusted = true;
    }
}
