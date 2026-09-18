package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.placeables.layouts.UnbuildableLayoutException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;

/**
 * A builder for {@link AxisMapper}s.
 * <p>
 * An {@code AxisMapperBuilder} builds {@link AxisMapper}s and gives information about their possible sizes.
 */
public interface AxisMapperBuilder {
    /**
     * Creates an {@link AxisMapper} for the given size.
     * <p>
     * Resulting {@link AxisMapper} will not be offset (it will start at position 0)
     *
     * @param size wanted size for {@link AxisMapper}
     * @return built {@link AxisMapper}
     * @throws UnbuildableLayoutException if not able to build for this size.
     */
    AxisMapper build(int size) throws UnbuildableLayoutException;

    /**
     * Returns the greatest buildable size under given size.
     * <p>
     * Not all sizes are suitable for building.
     * This method will provide information about what is possible.
     *
     * @param size Testing size
     * @return Greatest buildable size under testing size or {@code -1} if no suitable size found.
     */
    int maxSizeUnder(int size);

    /**
     * {@return the minimal buildable size}
     */
    int minimumSize();

    /**
     * {@return the starting point (usually 0 but may be different)}
     */
    int origin();
}
