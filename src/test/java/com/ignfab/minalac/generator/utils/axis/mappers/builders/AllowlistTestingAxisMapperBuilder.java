package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import java.util.Arrays;

import com.ignfab.minalac.generator.placeables.layouts.UnbuildableLayoutException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;

/**
 * A testing AxisMapperBuilder that only allows given sizes.
 */
public class AllowlistTestingAxisMapperBuilder implements AxisMapperBuilder {
    private final int[] allowedLength;
    private final int origin;

    /**
     * Creates a new {@code AllowlistTestingAxisMapperBuilder}.
     *
     * @param allowedLength Array of only allowed sizes.
     * @param origin Origin position
     */
    public AllowlistTestingAxisMapperBuilder(int[] allowedLength, int origin) {
        this.allowedLength = allowedLength;
        this.origin = origin;
        Arrays.sort(this.allowedLength);
        if (this.allowedLength[0] < 0)
            throw new RuntimeException("Can not contain negative length");
    }

    /**
     * Creates a new {@code AllowlistTestingAxisMapperBuilder} with origin at 0.
     *
     * @param allowedLength Array of only allowed sizes.
     */
    public AllowlistTestingAxisMapperBuilder(int[] allowedLength) {
        this(allowedLength, 0);
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableLayoutException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int maxSizeFittingUnder(int threshold) {
        if (threshold < allowedLength[0])
            return -1;
        for (int i = allowedLength.length - 1; i >= 0; i--)
            if (allowedLength[i] <= threshold)
                return allowedLength[i];
        throw new RuntimeException("It should not happen");
    }

    @Override
    public int minimumSize() {
        return allowedLength[0];
    }

    @Override
    public int origin() {
        return origin;
    }
}
