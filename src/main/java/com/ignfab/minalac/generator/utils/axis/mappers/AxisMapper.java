package com.ignfab.minalac.generator.utils.axis.mappers;

/**
 * An axis mapper maps a mono dimensional position in a composite structure into a substructure index and position in substructure.
 * <p>
 * It also provides a list of intervals with their sizes. This could represent a list of dimension, on the given axis, of repeated or placed structures.
 */
public interface AxisMapper {
    /**
     * Maps given position on axis to an underlying interval.
     *
     * @param position position to map
     * @return {@link MappedIndex} combining the interval index and in interval position.
     */
    Mapped map(int position);

    /**
     *{@return list of underlying intervals sizes}
     */
    int[] intervals();


    /**
     * {@return minimum valid position for this axis mapper}
     */
    int minimum();

    /**
     * {@return maximum valid position for this axis mapper}
     */
    default int maximum() {
        return minimum() + size() - 1;
    };

    /**
     * {@return size of the axis mapper}
     */
    int size();

    /**
     * Tells if position could be mapped.
     *
     * @param position position to test
     * @return true if position could be mapped
     */
    default boolean contains(int position) {
        return position >= minimum() && position <= maximum();
    }

    /**
     * A mapped index.
     *
     * @param index Structure index (which structure index is mapped to)
     * @param position In structure position
     */
    record Mapped(int index, int position){};
}
