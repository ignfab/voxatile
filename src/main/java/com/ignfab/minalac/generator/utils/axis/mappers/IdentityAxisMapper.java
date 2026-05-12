package com.ignfab.minalac.generator.utils.axis.mappers;

/**
 * An {@link AxisMapper} that does an identity mapping.
 * <p>
 * It maps any position into interval number 0, at the same position (it keeps same origin as underlying interval)
 */
public class IdentityAxisMapper implements AxisMapper {
    private final int minimum;
    private final int size;
    private final int[] intervals;

    /**
     * Creates a new {@code IdentityIndexMapper}.
     *
     * @param minimum Start position of the underlying interval
     * @param size Size of the underlying (and so mapper) interval
     */
    public IdentityAxisMapper(int minimum, int size) {
        if (size < 0)
            throw new IllegalArgumentException("length can not be negative");
        this.size = size;
        this.minimum = minimum;

        // I think that this TODO is resolved, but unsure, leaving it just in case.
        // TODO-PR-Facade-OLD: How offset influences intervals? (anyway we should loose offset as soon as we have several intervals)
        // We should not use intervals if offset != 0
        intervals = (size == 0) ? new int[0] : new int[] { size };
    }

    @Override
    public Mapped map(int position) {
        if (minimum > position || position >= minimum + size)
            throw new IndexOutOfBoundsException("%s Provided position is out of bounds (position %d, min %d, size %d)".formatted(this, position, minimum, size));
        return new Mapped(0, position);
    }

    @Override
    public int[] intervals() {
        return intervals;
    }

    @Override
    public int minimum() {
        return minimum;
    }

    @Override
    public int size() {
        return size;
    }
}
