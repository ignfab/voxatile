package com.ignfab.minalac.generator.placeables.resized.builders;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;
import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;

public class TestingIndexMapperBuilder implements IndexMapperBuilder {
    private int min;
    private int max;
    // private SortedSet<Integer> possibleSizes;


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

    /*
    public TestingIndexMapperBuilder(Integer... min) {
        this.possibleSizes = new TreeSet<>(Arrays.asList(min));
    }

    public TestingIndexMapperBuilder(int min, int max) {
        if (max < min)
            throw new RuntimeException("max should be greater than min");
        List<Integer> list = new ArrayList<>();
        for (int v = min; v <= max; v++) {
            list.add(v);
        }
        this.possibleSizes = new TreeSet<>(list);
    }*/

    @Override
    public IndexMapper build(int size) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int maxSizeUnder(int size) {
        // return possibleSizes.stream().filter(i -> i <= size).mapToInt(Integer::intValue).max().orElse(-1);
        if (size < min)
            return -1;
        return Math.min(size, max);
    }

    @Override
    public int minimumSize() {
        // return possibleSizes.first();
        return min;
    }
}
