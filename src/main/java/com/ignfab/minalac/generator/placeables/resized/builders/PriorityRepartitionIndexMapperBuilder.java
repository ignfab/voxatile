package com.ignfab.minalac.generator.placeables.resized.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;
import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.mappers.LengthIndexMapper;

public class PriorityRepartitionIndexMapperBuilder implements IndexMapperBuilder {
    private final IndexMapperBuilder[] builders;
    private final TreeMap<Integer, List<Integer>> map = new TreeMap<>(Collections.reverseOrder());
    private final int minimalSize;

    public PriorityRepartitionIndexMapperBuilder(IndexMapperBuilder[] builders, int[] priorities) {
        this.builders = builders;
        int sum = 0;
        for (int i = 0; i < priorities.length; i++) {
            map.computeIfAbsent(priorities[i], k -> new ArrayList<>()).add(i);
            sum = sum + builders[i].minimumSize();
        }
        minimalSize = sum;
    }

    @Override
    public IndexMapper build(int size) {
        int remainder = size;
        int[] lengths = new int[builders.length];
        for (int i = 0; i < lengths.length; i++) {
            int currentMinSize = builders[i].minimumSize();
            lengths[i] = currentMinSize;
            remainder = remainder - currentMinSize;
        }
        if (remainder < 0)
            throw new RuntimeException("Requested size is not enough");

        // if == 0 good
        if (remainder != 0) {
            for (Integer key : map.keySet()) {
                List<Integer> priorityBuilderIndexes = map.get(key);
                int n = priorityBuilderIndexes.size();
                for (Integer index : priorityBuilderIndexes) {
                    // TODO: C'est peut etre mieux de faire une variable du reste pour les candidats
                    // L'arrondi supérieur c'est pour assurer que l'on donne au premier un peu plus par exemple 17 : 6 - 6 - 5 ou  16 : 6 - 5 - 5
                    // Ca amrche bien quand ce qui est retranché est le subremainer (sauf que ici c'est le added qui est retranché
                    // Donc à verifier si il y a un probleme ou pas
                    int subRemainder = (remainder % n == 0) ? remainder / n : (remainder / n) + 1;
                    int maxPossibleSize = builders[index].maxSizeUnder(lengths[index] + subRemainder);
                    // maxPossibleSize peut être 0 ou négatif, en revanche lengths est au minium 0
                    if (lengths[index] < maxPossibleSize) {
                        int added = maxPossibleSize - lengths[index];
                        lengths[index] = maxPossibleSize;
                        remainder = remainder - added;
                    }
                    n--;
                }
            }
        }

        if (remainder != 0)
            throw new RuntimeException("Requested size is not enough, failed to equality distribute it " + remainder);

        return new LengthIndexMapper(lengths);
    }

    @Override
    public int maxSizeUnder(int size) {
        // TODO: refactoriser ou faire un systeme de "cache";
        int remainder = size;
        int[] lengths = new int[builders.length];
        for (int i = 0; i < lengths.length; i++) {
            int currentMinSize = builders[i].minimumSize();
            lengths[i] = currentMinSize;
            remainder = remainder - currentMinSize;
        }
        if (remainder < 0)
            return 0;

        if (remainder != 0) {
            for (Integer key : map.keySet()) {
                List<Integer> priorityBuilderIndexes = map.get(key);
                int n = priorityBuilderIndexes.size();
                for (Integer index : priorityBuilderIndexes) {
                    int subRemainder = (remainder % n == 0) ? remainder / n : (remainder / n) + 1;
                    int maxPossibleSize = builders[index].maxSizeUnder(lengths[index] + subRemainder);
                    if (lengths[index] < maxPossibleSize) {
                        int added = maxPossibleSize - lengths[index];
                        lengths[index] = maxPossibleSize;
                        remainder = remainder - added;
                    }
                    n--;
                }
            }
        }

        return size - remainder;
    }

    @Override
    public int minimumSize() {
        return minimalSize;
    }

    public static void main(String[] args) {
        IndexMapperBuilder dummy1 = new DummyIndexMapperBuilder(0, 100);
        IndexMapperBuilder dummy2 = new DummyIndexMapperBuilder(1, 2);
        IndexMapperBuilder dummy3 = new DummyIndexMapperBuilder(3, 3);
        IndexMapperBuilder[] builders = new IndexMapperBuilder[]{dummy1, dummy2, dummy3};
        int[] priority = new int[]{0, 1, 2};

        PriorityRepartitionIndexMapperBuilder prio = new PriorityRepartitionIndexMapperBuilder(builders, priority);

        IndexMapper im = prio.build(6);

        System.out.println(im.structures());

        for (int c = 0; c < im.size(); c++) {
            System.out.println(c + " -> " + im.placeable(c));
        }
    }
}
