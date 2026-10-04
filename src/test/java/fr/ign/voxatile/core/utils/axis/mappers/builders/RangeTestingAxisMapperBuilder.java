package fr.ign.voxatile.core.utils.axis.mappers.builders;

import fr.ign.voxatile.core.utils.axis.mappers.AxisMapper;

public class RangeTestingAxisMapperBuilder implements AxisMapperBuilder {
    private final int min;
    private final int max;
    private final int origin;

    public RangeTestingAxisMapperBuilder(int min, int max, int origin) {
        if (max < min)
            throw new IllegalArgumentException("max should be greater than min");
        this.min = min;
        this.max = max;
        this.origin = origin;
    }

    public RangeTestingAxisMapperBuilder(int min, int max) {
        this(min, max, 0);
    }

    public RangeTestingAxisMapperBuilder(int size) {
        min = size;
        max = size;
        origin = 0;
    }

    @Override
    public AxisMapper build(int size) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int maxSizeFittingUnder(int threshold) {
        if (threshold < min)
            return -1;
        return Math.min(threshold, max);
    }

    @Override
    public int minimumSize() {
        return min;
    }

    @Override
    public int origin() {
        return origin;
    }
}
