package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.SizesAxisMapper;

public class PriorityRepartitionAxisMapperBuilder implements AxisMapperBuilder {
    private final AxisMapperBuilder[] builders;
    private final TreeMap<Integer, List<Integer>> priorities = new TreeMap<>(Collections.reverseOrder());
    private final int[] minLengths;
    private final int minimalSize;

    public PriorityRepartitionAxisMapperBuilder(AxisMapperBuilder[] builders, int[] priorities) {
        if (builders.length != priorities.length)
            throw new IllegalArgumentException("Provide array must be same length");
        this.builders = builders;
        int sum = 0;
        minLengths = new int[builders.length];
        for (int i = 0; i < priorities.length; i++) {
            this.priorities.computeIfAbsent(priorities[i], k -> new ArrayList<>()).add(i);
            minLengths[i] = builders[i].minimumSize();
            sum = sum + minLengths[i];
        }
        minimalSize = sum;
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableException {
        if (size < minimalSize)
            throw new UnbuildableException("Requested size is not enough");

        DistributionResult result = compute(size);
        if (result.remainder != 0)
            throw new UnbuildableException("Could not distribute remainder");
        return new SizesAxisMapper(result.lengths);
    }

    @Override
    public int maxSizeUnder(int size) {
        if (size < minimalSize)
            return -1;
        // TODO-13 : Initialement j'vais mis ce test car c'était un espece de copié-collé de l'equaliseur ou on peutt pas faire une division par zero pour connaitre le nombre de sous-segment
        // Ici la logique de repartition est différente : un IMB 0..2 peut avoir une taille min de 0 mais pas "vide" (!= 0..0) Donc on peut laisser l'algo faire la repartition
        // Je laisse le commentaire pour revenir dessus au besoin meme si il est faux
        // if (minimalSize == 0)
        //    return 0;
        int remainder = compute(size).remainder;
        return size - remainder;
    }

    @Override
    public int minimumSize() {
        return minimalSize;
    }

    private DistributionResult compute(int size) {
        // We start with minimum size for everyone.
        int remaining = size - minimalSize;
        if (remaining == 0) return new DistributionResult(minLengths, 0);
        int[] lengths = new int[minLengths.length];

        // Now we distribute remaining by prirority order
        for (Map.Entry<Integer, List<Integer>> priority: priorities.entrySet()) {
            int count = priority.getValue().size();

            for (int index : priority.getValue()) {
                int possible = builders[index].maxSizeUnder(minLengths[index] + Math.ceilDiv(remaining, count));

                // TODO: Check: possible could be negative?

                remaining += minLengths[index] - possible;
                lengths[index] = possible;
                count--;
            }
        }
        return new DistributionResult(lengths, remaining);
    }

    private record DistributionResult(int[] lengths, int remainder) {
    }
}
