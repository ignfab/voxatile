package com.ignfab.minalac.generator.utils.axis.mappers;

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
        if (0 > position || position >= size)
            throw new IndexOutOfBoundsException("Provided position is out of bounds");
        return new Mapped(0, position + min);
    }
}
