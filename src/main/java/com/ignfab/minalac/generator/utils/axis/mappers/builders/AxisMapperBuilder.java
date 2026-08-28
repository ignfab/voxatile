package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
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
     * Resulting {@link AxisMapper} will not be offsetted (it will start at position 0)
     *
     * @param size wanted size for {@link AxisMapper}
     * @return built {@link AxisMapper}
     * @throws UnbuildableException if not able to build for this size.
     */
    AxisMapper build(int size) throws UnbuildableException;

    /**
     * Returns the greatest buildable size under given size.
     * <p>
     * Not all sizes are suitable for building. This methods will provide information about what is possible.
     * Returns -1 if no suitable size found.
     * @param size Testing size
     * @return Greatest buildable size under testing size.
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
