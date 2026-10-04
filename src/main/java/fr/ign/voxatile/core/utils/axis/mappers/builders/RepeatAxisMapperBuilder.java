package fr.ign.voxatile.core.utils.axis.mappers.builders;

import java.util.Arrays;

import fr.ign.voxatile.core.placeables.layouts.UnbuildableLayoutException;
import fr.ign.voxatile.core.utils.axis.mappers.AxisMapper;
import fr.ign.voxatile.core.utils.axis.mappers.SizesAxisMapper;

/**
 * An {@link AxisMapperBuilder} that repeats underlying {@link AxisMapperBuilder} as many times as possible.
 */
public class RepeatAxisMapperBuilder implements AxisMapperBuilder {
    private final AxisMapperBuilder underlying;
    private final int minSize;
    private final int maxOccur;

    /**
     * Creates a new {@code RepeatAxisMapperBuilder}.
     *
     * @param underlying Undelying {@link AxisMapperBuilder} to repeat
     * @param minOccur Minimum number of occurrences
     * @param maxOccur Maximum number of occurrences
     * @throws UnbuildableLayoutException if underlying mapper builder cannot be repeated (0 sized)
     */
    public RepeatAxisMapperBuilder(AxisMapperBuilder underlying, int minOccur, int maxOccur) throws UnbuildableLayoutException {
        // Another way to do it to allow it is to instantiate a SizesAxisMapper with a length of 0.
        // If changed, StretcherAxisMapperBuilder should have same behavior
        if (underlying.minimumSize() <= 0)
            throw new UnbuildableLayoutException("Underlying has a minimum size of zero. Can not be repeated");
        this.maxOccur = maxOccur;
        this.underlying = underlying;
        minSize = underlying.minimumSize() * minOccur;
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableLayoutException {
        if (size < minSize)
            throw new UnbuildableLayoutException("Requested size is not enough");

        DistributionResult result = compute(size, underlying.minimumSize());
        int remainder = result.remainder;

        if (remainder != 0)
            throw new UnbuildableLayoutException("Requested size is either not enough or too large. Failed to distribute remainder");

        return new SizesAxisMapper(result.lengths);
    }

    @Override
    public int maxSizeFittingUnder(int threshold) {
        if (threshold < minSize)
            return -1;

        int underlyingMin = underlying.minimumSize();
        if (underlyingMin == 0) return 0;

        DistributionResult result = compute(threshold, underlyingMin);
        return threshold - result.remainder;
    }


    @Override
    public int minimumSize() {
        return minSize;
    }

    @Override
    public int origin() {
        return 0;
    }

    private DistributionResult compute(int size, int underlyingMin) {
        if (underlyingMin <= 0)
            throw new UnsupportedOperationException("Can not repeat a layout that has a minimal size equal or below zero");
        int count = Math.min(size / underlyingMin, maxOccur);
        // Modulo not used since maxOccur can be below (size / underlyingMin)
        int remaining = size - count * underlyingMin;

        int[] lengths = new int[count];
        Arrays.fill(lengths, underlyingMin);

        for (int index = 0; index < lengths.length; index++) {
            // Math.ceilDiv not available in Java 17
            int possible = underlying.maxSizeFittingUnder(underlyingMin + (remaining + count - 1) / count);
            // distributedRemaining is (lengths[index] - possible)
            remaining += lengths[index] - possible;
            lengths[index] = possible;
            count--;
        }

        return new DistributionResult(lengths, remaining);
    }

    private record DistributionResult(int[] lengths, int remainder) {}
}
