package com.ignfab.minalac.generator.utils.axis.mappers;

/**
 * An {@link AxisMapper} that does an identity mapping.
 * <p>
 * It maps any position into interval number 0, at the same position (it keeps same origin as underlying interval)
 */
public class IdentityAxisMapper implements AxisMapper {
    protected final int min;
    protected final int size;
    private final int[] intervals;

    /**
     * Creates a new {@code IdentityIndexMapper}.
     *
     * @param origin Start position of the underlying interval
     * @param size Size of the underlying (and so mapper) interval
     */
    public IdentityAxisMapper(int min, int size) {
        if (size < 0)
            throw new IllegalArgumentException("length can not be negative");
        this.size = size;
        this.min = min;

        // TODO: How offset influences intervals? (anyway we should loose offset as soon as we have several intervals)
        // We should not use intervals if offset != 0
        intervals = (size == 0) ? new int[0] : new int[] { size };
    }

    @Override
    public Mapped map(int position) {
        if (min > position || position >= min + size)
            throw new IndexOutOfBoundsException("%s Provided position is out of bounds (position %d, min %d, size %d)".formatted(this, position, min, size));
        return new Mapped(0, position);
    }

    @Override
    public int[] intervals() {
        return intervals;
    }

    @Override
    public int min() {
        return min;
    }

    @Override
    public int size() {
        return size;
    }
}
