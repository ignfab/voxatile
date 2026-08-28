package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.placeables.layouts.UnbuildableLayoutException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.IdentityAxisMapper;

/**
 * An {@link AxisMapperBuilder} made of multiple {@link AxisMapperBuilder}s having the same dimensions.
 * <p>
 * Underlying builders must all have the same origin or {@code AdjustAxisMapperBuilder} will not be
 * able to adjust them as we have no stretcher able to change origin yet.
 */
public class AdjustAxisMapperBuilder implements AxisMapperBuilder {
    private final AxisMapperBuilder[] builders;
    private final int minimumSize;
    private final int origin;

    /**
     * Creates a new {@code AdjustAxisMapperBuilder}.
     * @param builders underlying {@link AxisMapperBuilder} to adjust
     * @throws UnbuildableLayoutException if underlying builders can't adjust to each other
     */
    public AdjustAxisMapperBuilder(AxisMapperBuilder... builders) throws UnbuildableLayoutException {
        this.builders = builders;

        if (builders.length == 0) {
            origin = 0;
            minimumSize = 0;
            return;
        }

        int origin = builders[0].origin();
        int minimumSize = 0;

        for (AxisMapperBuilder builder : builders) {
            if (origin != builder.origin())
                throw new UnbuildableLayoutException("All origins must be the same");
            minimumSize = Math.max(minimumSize, builder.minimumSize());
        }

        // Check
        minimumSize = maxSizeFittingUnder(minimumSize);

        if (minimumSize < 0)
            throw new UnbuildableLayoutException("Unable to adjust dimensions (builders don't agree)");

        this.minimumSize = minimumSize;
        this.origin = origin;
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableLayoutException {
        if (size < 0)
            throw new IllegalArgumentException("Size must be positive or zero");

        int possible = maxSizeFittingUnder(size);
        if (size != possible)
            throw new UnbuildableLayoutException("Impossible to build for this size (%d, possible %d)".formatted(size, possible));

        return new IdentityAxisMapper(origin, size);
    }

    @Override
    public int maxSizeFittingUnder(int threshold) {
        if (threshold < 0)
            return threshold;
        int minCandidate = threshold;
        for (AxisMapperBuilder builder : builders)
            minCandidate = Math.min(minCandidate, builder.maxSizeFittingUnder(threshold));
        if (minCandidate < 0 || minCandidate == threshold)
            return minCandidate;

        return maxSizeFittingUnder(minCandidate);
    }

    @Override
    public int minimumSize() {
        return minimumSize;
    }

    @Override
    public int origin() {
        return origin;
    }
}
