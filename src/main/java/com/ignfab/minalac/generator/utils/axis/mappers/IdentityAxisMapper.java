package com.ignfab.minalac.generator.utils.axis.mappers;

/**
 * An {@link AxisMapper} that does an identity mapping.
 * <p>
 * It maps any position into interval number 0, at the same position (it keeps same origin as underlying interval)
 */
public class IdentityAxisMapper extends AxisMapper {

    /**
     * Creates a new {@code IdentityAxisMapper}.
     *
     * @param minimum Start position of the underlying interval
     * @param size Size of the underlying (and so mapper) interval
     */
    public IdentityAxisMapper(int minimum, int size) {
        super(minimum, size, (size == 0) ? new int[0] : new int[] { size });
    }

    @Override
    protected Mapped mapUnchecked(int position) {
        return new Mapped(0, position);
    }
}
