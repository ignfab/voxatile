package com.ignfab.minalac.generator.placeables.resized.builders;


import com.ignfab.minalac.generator.placeables.resized.IndexMapper;
import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;

public class TestingIndexMapperBuilder implements IndexMapperBuilder {
    private final int min;
    private final int max;

    public TestingIndexMapperBuilder(int min, int max) {
        if (max < min)
            throw new RuntimeException("max should be greater than min");
        this.min = min;
        this.max = max;
    }

    public TestingIndexMapperBuilder(int size) {
        min = size;
        max = size;
    }

    @Override
    public IndexMapper build(int size) {
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
}
