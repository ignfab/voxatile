package com.ignfab.minalac.generator.placeables.resized.builders;

import java.util.Arrays;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;
import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.UnresizableStructureException;
import com.ignfab.minalac.generator.placeables.resized.mappers.LengthIndexMapper;

public class EqualizerIndexMapperBuilder implements IndexMapperBuilder {
    private final IndexMapperBuilder underlying;
    private final int minSize;

    public EqualizerIndexMapperBuilder(IndexMapperBuilder underlying, int minOccur) {
        this.underlying = underlying;
        int underlyingSize = underlying.minimumSize();
        if (underlyingSize == 0)
            minSize = 0;
        else
            minSize = underlyingSize * minOccur;
    }

    @Override
    public IndexMapper build(int size) throws UnresizableStructureException {
        if (size < minSize)
            throw new UnresizableStructureException("Requested size is not enough");

        int underlyingMin = underlying.minimumSize();
        if (underlyingMin == 0)
            return new LengthIndexMapper(0);

        DistributionResult result = compute(size, underlyingMin);
        int remainder = result.remainder;

        if (remainder != 0)
            throw new UnresizableStructureException("Requested size iis either not enough or too large. Failed to distribute remainder");

        return new LengthIndexMapper(result.lengths);
    }

    @Override
    public int maxSizeUnder(int size) {
        if (size < minSize)
            return -1;

        int underlyingMin = underlying.minimumSize();
        if (underlyingMin == 0)
            return 0;

        DistributionResult result = compute(size, underlyingMin);
        return size - result.remainder;
    }


    @Override
    public int minimumSize() {
        return minSize;
    }

    // underlyingMin should be strictly positive
    // TODO-12 : Revoir cette partie : elle est testée à la louche
    private DistributionResult compute(int size, int underlyingMin) {
        int n = size / underlyingMin;
        int remainder = size % underlyingMin;

        int[] lengths = new int[n];
        Arrays.fill(lengths, underlyingMin);

        for (int i = 0; i < n; i++) {
            int subRemainder = (remainder % n == 0) ? remainder / n : (remainder / n) + 1;
            int maxPossibleSize = underlying.maxSizeUnder(underlyingMin + subRemainder);
            if (lengths[i] < maxPossibleSize) {
                int added = maxPossibleSize - lengths[i];
                lengths[i] = maxPossibleSize;
                remainder = remainder - added;
            }
            n--;
        }

        return new DistributionResult(lengths, remainder);
    }

    private record DistributionResult(int[] lengths, int remainder) {
    }

    ;

    public static void main(String[] args) throws UnresizableStructureException {
        IndexMapperBuilder dummy1 = new DummyIndexMapperBuilder(2, 4);
        IndexMapperBuilder a = new EqualizerIndexMapperBuilder(dummy1, 0);
        IndexMapper im = a.build(10);

        System.out.println(im.structures());

        for (int c = 0; c < im.size(); c++) {
            System.out.println(c + " -> " + im.placeable(c));
        }
    }
}
