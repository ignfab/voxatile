package com.ignfab.minalac.generator.utils.axis.mappers;

/**
 * An {@link AxisMapper} that maps a position into a concatenation of intervals with given sizes.
 * <p>
 * {@code SizesAxisMapper} does not manage origins of underlying intervals.
 * They would be moved anyway. So origin of this {@link AxisMapper} is always 0.
 */
public class SizesAxisMapper implements AxisMapper {
    private final int size;
    private final int[] intervals;

    /**
     * Creates a new {@code SizesAxisMapper}.
     * <p>
     * A {@code SizesAxisMapper} maps position into a succession of intervals of various sizes.
     *
     * @param sizes list of sizes of intervals composing the axis.
     */
    public SizesAxisMapper(int... sizes) {
        // TODO-4: Voir s'il faut ou pas ignorer les zeros
        // Arrays.stream(lengths).filter( l -> l >=0).toArray();
        // TODO-5 Faire en une passe et améliorer

        intervals = sizes;

        int size = 0;
        for (int index = 0; index < intervals.length; index++) {
            if (intervals[index] < 0)
                throw new IllegalArgumentException("length can not be negative");
            size += intervals[index];
        }
        this.size = size;
    }

    @Override
    public Mapped map(int position) {
        if (0 > position || position >= size)
            throw new IndexOutOfBoundsException("Provided position is out of bounds");

        for (int index = 0; index < intervals.length; index++) {
            int size = intervals[index];
            if (position < size)
                return new Mapped(index, position);
            position -= size;
        }

        // Will never be reached
        throw new IndexOutOfBoundsException("This is a bug");
    }

    @Override
    public int[] intervals() {
        return intervals;
    }

    @Override
    public int minimum() {
        return 0;
    }

    @Override
    public int size() {
        return size;
    }
}
