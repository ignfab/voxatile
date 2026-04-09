package com.ignfab.minalac.generator.utils.axis.mappers;

/**
 * An {@link AxisMapper} that maps position with an offset.
 * <p>
 * It maps any position into interval number 0, it makes position 0 correspond to underlying interval origin.
 */
public class OffsetAxisMapper extends IdentityAxisMapper {
    /**
     * Creates a new {@code OffsetAxisMapper}.
     *
     * @param origin Start position of the underlying interval
     * @param size Size of the underlying (and so mapper) interval
     */
    public OffsetAxisMapper(int origin, int size) {
        super(origin, size);
    }

    @Override
    public Mapped map(int position) {
        if (0 > position || position >= size())
            throw new IndexOutOfBoundsException("Provided position is out of bounds");
        return new Mapped(0, position + minimum());
    }
}
