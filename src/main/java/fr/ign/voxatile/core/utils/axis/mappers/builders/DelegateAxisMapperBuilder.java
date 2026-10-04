package fr.ign.voxatile.core.utils.axis.mappers.builders;

import fr.ign.voxatile.core.placeables.layouts.UnbuildableLayoutException;
import fr.ign.voxatile.core.utils.axis.mappers.AxisMapper;
import fr.ign.voxatile.core.utils.axis.mappers.IdentityAxisMapper;

/**
 * An {@link AxisMapperBuilder} inheriting size constraints from another {@link AxisMapperBuilder}.
 */
public class DelegateAxisMapperBuilder implements AxisMapperBuilder {
    private final AxisMapperBuilder delegatee;

    /**
     * Creates a new {@code DelegateAxisMapperBuilder} for a given {@link AxisMapperBuilder}.
     *
     * @param delegatee an {@link AxisMapperBuilder} from which size constraints are inherited
     */
    public DelegateAxisMapperBuilder(AxisMapperBuilder delegatee) {
        this.delegatee = delegatee;
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableLayoutException {
        if (maxSizeFittingUnder(size) > size)
            throw new UnbuildableLayoutException("Builder could not fit size=%d (Builder is %s)".formatted(size, delegatee));
        return new IdentityAxisMapper(origin(), size);
    }

    @Override
    public int maxSizeFittingUnder(int threshold) {
        return delegatee.maxSizeFittingUnder(threshold);
    }

    @Override
    public int minimumSize() {
        return delegatee.minimumSize();
    }

    @Override
    public int origin() {
        return delegatee.origin();
    }
}
