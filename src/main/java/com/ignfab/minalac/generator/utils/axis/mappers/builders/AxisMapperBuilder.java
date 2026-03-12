package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;

/**
 * A builder for {@link AxisMapper}s.
 * <p>
 * An {@code AxisMapperBuilder} builds {@link AxisMapper}s and gives information about their possible sizes.
 */
public interface AxisMapperBuilder {
    // TODO-10 : Doit lever une exception UnresizableStructureException ?
    /**
     * Creates an {@link AxisMapper} for the given size.
     *
     * @param size wanted size for {@link AxisMapper}
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
     * @returns the minimal buildable size.
     */
    // TODO-11 : Really ? Always positive or zero?
    // Should always be postive or equals to zero?
    int minimumSize();
}
