package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.IdentityAxisMapper;

/**
 * An {@link AxisMapperBuilder} made of multiple overlayed {@link AxisMapperBuilder}s.
 */
public class KeepAxisMapperBuilder implements AxisMapperBuilder {
    private final int minimumSize;
    private final int origin;

    /**
     * Creates a new {@code OverlayAxisMapperBuilder}.
     * @param builders underlying {@link AxisMapperBuilder} to overlay
     */
    public KeepAxisMapperBuilder(AxisMapperBuilder... builders) {
        if (builders.length == 0) {
            origin = 0;
            minimumSize = 0;
        } else {
            int minimum = Integer.MAX_VALUE;
            int maximum = Integer.MIN_VALUE;

            for (AxisMapperBuilder builder : builders) {
                minimum = Math.min(minimum, builder.origin());
                maximum = Math.max(maximum, builder.origin() + builder.minimumSize());
            }
            origin = minimum;
            minimumSize = maximum - minimum;
        }
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableException {
        if (size < 0)
            throw new IllegalArgumentException("Size must be positive or zero");
        // Must be quite permissive or wont be able to render stuff with various sizes
        if (size < minimumSize)
            throw new UnbuildableException("Impossible to build for this size (%d, must be at least %d)".formatted(size, minimumSize));

        return new IdentityAxisMapper(origin, size);
    }

    @Override
    public int maxSizeUnder(int size) {
        return size < minimumSize ? - 1 : size;
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
