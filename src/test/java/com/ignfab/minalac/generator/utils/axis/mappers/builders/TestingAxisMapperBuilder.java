package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;

public class TestingAxisMapperBuilder implements AxisMapperBuilder {
    private int min;
    private int max;
    private int origin;

    public TestingAxisMapperBuilder(int min, int max, int origin) {
        if (max < min)
            throw new RuntimeException("max should be greater than min");
        this.min = min;
        this.max = max;
        this.origin = origin;
    }

    public TestingAxisMapperBuilder(int min, int max) {
        this(min, max, 0);
    }

    public TestingAxisMapperBuilder(int size) {
        min = size;
        max = size;
    }

    @Override
    public AxisMapper build(int size) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int maxSizeUnder(int size) {
        if (size < min)
            return -1;
        return Math.min(size, max);
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
