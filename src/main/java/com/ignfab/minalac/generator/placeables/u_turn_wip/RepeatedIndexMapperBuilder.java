package com.ignfab.minalac.generator.placeables.u_turn_wip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

public class RepeatedIndexMapperBuilder implements IndexMapperBuilder {
    IndexMapperBuilder base;
    int minSize;
    int maxSize;

    public RepeatedIndexMapperBuilder(IndexMapperBuilder base, int minOccur) {
        this.base = base;
        this.minSize = base.minimalSize() * minOccur;
        this.maxSize = Integer.MAX_VALUE;
    }

    @Override
    public IndexMapper build(int size) {
        if (size < minSize)
            throw new RuntimeException("Not possible");

        // TODO DEMAIN :
        // Demande à enfant si possible d'ajouter
        // Dans la mesure du possible faire une repartition equitable

        int r = size % minSize;
        int n = size / minSize;
        if (r == 0) {
            int[] lengths = new int[n];
            Arrays.fill(lengths, minSize);
            return new LengthIndexMapper(lengths);
        }


        if (n == 1) {
            if (base.ask(size) == size)
                return new LengthIndexMapper(size);
            else
                throw new RuntimeException("impossible");
        }

        if (n > 1) {
            List<Integer> list = new ArrayList<>();
            int[] lengths = new int[n];
            for (int i = 1; i <= r; i ++) {
                if (base.ask(minSize + i) == minSize + i)
                    list.add(minSize + i);
            }
            int p = (r % n == 0) ? r / n : (r / n) + 1;
            if (list.contains(p)) {
                for (int i = 0; i < n; i++) {
                    lengths[i] = minSize + p;
                    r = r - p;
                    if (r <= 0)
                        p = 0;
                    else if (r < p)
                        p = r;
                }
                return new LengthIndexMapper(size);
            }
        }

        throw new RuntimeException("Impossible;");
    }

    @Override
    public int ask(int size) {
        if (size == 0 || size < minSize) return 0;
        int r = size % minSize;
        for (int i = 0; i <= r; i ++) {
            if (base.ask(minSize + i) == minSize + i)
                return size;
        }
        return 0;
    }

    @Override
    public int minimalSize() {
        return minSize;
    }

    public static void main(String[] args) {
        IndexMapperBuilder dummy1 = new DummyIndexMapperBuilder(2, 4);
        IndexMapperBuilder a = new RepeatedIndexMapperBuilder(dummy1, 2);
        IndexMapper im = a.build(8);

        System.out.println(im.structure());

        for (int c = 0; c < im.size(); c++) {
            System.out.println(c + " -> " + im.placeable(c));
        }
    }
}
