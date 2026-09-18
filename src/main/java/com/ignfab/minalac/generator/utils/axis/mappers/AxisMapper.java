package com.ignfab.minalac.generator.utils.axis.mappers;

/**
 * An axis mapper maps a mono dimensional position in a composite structure into a substructure index and position in substructure.
 * <p>
 * It also provides a list of intervals with their sizes. This could represent a list of dimension, on the given axis, of repeated or placed structures.
 */
public abstract class AxisMapper {
    private final int minimum;
    private final int size;
    private final int[] intervals;


    protected AxisMapper(int minimum, int size, int[] intervals) {
        if (size < 0)
            throw new IllegalArgumentException("length can not be negative");

        this.minimum = minimum;
        this.size = size;
        this.intervals = intervals;
    }

    /**
     * Maps given position on axis to an underlying interval.
     *
     * @param position position to map
     * @return {@link Mapped} combining the interval index and in-interval position.
     */
    public Mapped map(int position) {
        if (!contains(position))
            throw new IndexOutOfBoundsException("%s Provided position is out of bounds (position %d, min %d, size %d)".formatted(this, position, minimum(), size()));
        return mapUnchecked(position);

    };

    /**
     * Same as {@code map} but does not check position is in axis mapper.
     *
     * @param position position to map
     * @return {@link Mapped} combining the interval index and in-interval position.
     */
    protected abstract Mapped mapUnchecked(int position);

    /**
     *{@return list of underlying intervals sizes}
     */
    public int[] intervals() {
        return intervals;
    }

    /**
     * {@return size of the axis mapper}
     */
    public int size() {
        return size;
    }

    /**
     * {@return minimum valid position for this axis mapper}
     */
    public int minimum() {
        return minimum;
    }

    /**
     * {@return maximum valid position for this axis mapper}
     */
    public int maximum() {
        return minimum + size - 1;
    }

    /**
     * Tells if position could be mapped.
     *
     * @param position position to test
     * @return true if position could be mapped
     */
    public boolean contains(int position) {
        return position >= minimum && position <= maximum();
    }

    /**
     * A mapped index.
     *
     * @param index Structure index (telling which structure index is mapped to)
     * @param position In structure position
     */
    public record Mapped(int index, int position) {}
}
