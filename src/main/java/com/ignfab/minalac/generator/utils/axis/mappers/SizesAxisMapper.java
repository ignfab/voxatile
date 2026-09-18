package com.ignfab.minalac.generator.utils.axis.mappers;

/**
 * An {@link AxisMapper} that maps a position into a concatenation of intervals with given sizes.
 * <p>
 * {@code SizesAxisMapper} does not manage origins of underlying intervals.
 * They would be moved anyway. So origin of this {@link AxisMapper} is always 0.
 */
public class SizesAxisMapper extends AxisMapper {
    /**
     * Creates a new {@code SizesAxisMapper}.
     * <p>
     * A {@code SizesAxisMapper} maps position into a succession of intervals of various sizes.
     *
     * @param sizes list of sizes of intervals composing the axis.
     */
    public SizesAxisMapper(int... sizes) {
        super(0, computeSize(sizes), sizes);
    }

    private static int computeSize(int[] intervals) {
        int size = 0;
        for (int index = 0; index < intervals.length; index++) {
            if (intervals[index] < 0)
                throw new IllegalArgumentException("length can not be negative");
            size += intervals[index];
        }
        return size;
    }

    @Override
    protected Mapped mapUnchecked(int position) {
        for (int index = 0; index < intervals().length; index++) {
            int size = intervals()[index];
            if (position < size)
                return new Mapped(index, position);
            position -= size;
        }

        // Will never be reached
        throw new IndexOutOfBoundsException("This is a bug");
    }
}
