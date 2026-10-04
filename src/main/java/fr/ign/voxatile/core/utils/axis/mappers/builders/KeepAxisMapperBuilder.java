package fr.ign.voxatile.core.utils.axis.mappers.builders;

import fr.ign.voxatile.core.placeables.layouts.UnbuildableLayoutException;
import fr.ign.voxatile.core.utils.axis.mappers.AxisMapper;
import fr.ign.voxatile.core.utils.axis.mappers.IdentityAxisMapper;

/**
 * An {@link AxisMapperBuilder} made of multiple unchanged and overlayed {@link AxisMapperBuilder}s.
 */
public class KeepAxisMapperBuilder implements AxisMapperBuilder {
    private final int minimumSize;
    private final int origin;

    /**
     * Creates a new {@code KeepAxisMapperBuilder}.
     * <p>
     * {@code KeepAxisMapperBuilder} keeps underlying {@link AxisMapperBuilder} size and offset unchanged.
     * This could be used for facade depth (we want to keep structure placement and size on the depth axis).
     *
     * @param builders underlying {@link AxisMapperBuilder}
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
    public AxisMapper build(int size) throws UnbuildableLayoutException {
        if (size < 0)
            throw new IllegalArgumentException("Size must be positive or zero");
        // Must be quite permissive or won't be able to render stuff with various sizes
        if (size < minimumSize)
            throw new UnbuildableLayoutException("Impossible to build for this size (%d, must be at least %d)".formatted(size, minimumSize));

        return new IdentityAxisMapper(origin, size);
    }

    @Override
    public int maxSizeFittingUnder(int threshold) {
        return threshold < minimumSize ? -1 : threshold;
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
