package com.ignfab.minalac.generator.placeables.resized.builders;

import java.util.Arrays;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;
import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.mappers.LengthIndexMapper;

public class EqualizerIndexMapperBuilder implements IndexMapperBuilder {
    IndexMapperBuilder underlying;
    int minSize;

    public EqualizerIndexMapperBuilder(IndexMapperBuilder underlying, int minOccur) {
        this.underlying = underlying;
        // TODO: pas sur de ça
        this.minSize = Math.max(underlying.minimumSize() * minOccur, underlying.minimumSize());
    }

    // TODO: Copié coller de PriorityRepartitionIndexMapperBuilder à revoir
    @Override
    public IndexMapper build(int size) {
        /*
        if (size < minSize)
            throw new RuntimeException("Requested size is not enough");*/

        int n = size / underlying.minimumSize();
        int remainder = size % underlying.minimumSize();

        int[] lengths = new int[n];
        Arrays.fill(lengths, underlying.minimumSize());

        for (int i = 0; i < n; i ++) {
            int subRemainder = (remainder % n == 0) ? remainder / n : (remainder / n) + 1;
            int maxPossibleSize = underlying.ask(underlying.minimumSize() + subRemainder);
            if (lengths[i] < maxPossibleSize) {
                int added = maxPossibleSize - lengths[i];
                lengths[i] = maxPossibleSize;
                remainder = remainder - added;
            }
            n--;
        }

        if (remainder != 0)
            throw new RuntimeException("Requested size is not enough, failed to equality distribute it " + remainder);

        return new LengthIndexMapper(lengths);
    }

    @Override
    public int ask(int size) {
        if (size < minSize)
            return 0;

        int n = size / underlying.minimumSize();
        int remainder = size % underlying.minimumSize();

        int[] lengths = new int[n];
        Arrays.fill(lengths, underlying.minimumSize());

        for (int i = 0; i < n; i ++) {
            int subRemainder = (remainder % n == 0) ? remainder / n : (remainder / n) + 1;
            int maxPossibleSize = underlying.ask(underlying.minimumSize() + subRemainder);
            if (lengths[i] < maxPossibleSize) {
                int added = maxPossibleSize - lengths[i];
                lengths[i] = maxPossibleSize;
                remainder = remainder - added;
            }
            n--;
        }

        return size - remainder;
    }

    @Override
    public int minimumSize() {
        return minSize;
    }

    public static void main(String[] args) {
        IndexMapperBuilder dummy1 = new DummyIndexMapperBuilder(2, 4);
        IndexMapperBuilder a = new EqualizerIndexMapperBuilder(dummy1, 0);
        IndexMapper im = a.build(1);

        System.out.println(im.structures());

        for (int c = 0; c < im.size(); c++) {
            System.out.println(c + " -> " + im.placeable(c));
        }
    }
}
