package fr.ign.voxatile.core.utils.axis.mappers.builders;

import fr.ign.voxatile.core.placeables.layouts.UnbuildableLayoutException;
import fr.ign.voxatile.core.utils.axis.mappers.AxisMapper;
import fr.ign.voxatile.core.utils.axis.mappers.IdentityAxisMapper;

/**
 * An {@link AxisMapperBuilder} for a constant size.
 */
public class ConstantAxisMapperBuilder implements AxisMapperBuilder {
    private final int size;
    private final int origin;

    /**
     * Creates a new {@code ConstantAxisMapperBuilder}.
     *
     * @param size Size of the built {@link AxisMapper}.
     * @param origin Starting position of underlying interval.
     */
    public ConstantAxisMapperBuilder(int size, int origin) {
        this.size = size;
        this.origin = origin;
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableLayoutException {
        if (size != this.size)
            throw new UnbuildableLayoutException("Requested size isn't equal to the intrinsic size");
        return new IdentityAxisMapper(origin, size);
    }

    @Override
    public int maxSizeFittingUnder(int threshold) {
        return threshold < size ? -1 : size;
    }

    @Override
    public int minimumSize() {
        return size;
    }

    @Override
    public int origin() {
        return origin;
    }
}
