package fr.ign.voxatile.core.utils.axis.mappers.builders;

import fr.ign.voxatile.core.placeables.layouts.UnbuildableLayoutException;
import fr.ign.voxatile.core.utils.axis.mappers.AxisMapper;

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
     * Returns the greatest buildable size fitting under given threshold.
     * <p>
     * Not all sizes are suitable for building.
     * This method will provide information about what is possible.
     *
     * @param threshold Testing size
     * @return Greatest buildable size lower than or equals to testing size or {@code -1} if no suitable size found.
     */
    int maxSizeFittingUnder(int threshold);

    /**
     * {@return the minimal buildable size}
     */
    int minimumSize();

    /**
     * {@return the starting point (usually 0 but may be different)}
     */
    int origin();
}
