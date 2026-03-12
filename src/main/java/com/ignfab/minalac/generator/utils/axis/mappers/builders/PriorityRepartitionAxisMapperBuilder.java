package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.SizesAxisMapper;

public class PriorityRepartitionAxisMapperBuilder implements AxisMapperBuilder {
    private final AxisMapperBuilder[] builders;
    private final TreeMap<Integer, List<Integer>> map = new TreeMap<>(Collections.reverseOrder());
    private final int[] minLengths;
    private final int minimalSize;

    public PriorityRepartitionAxisMapperBuilder(AxisMapperBuilder[] builders, int[] priorities) {
        if (builders.length != priorities.length)
            throw new IllegalArgumentException("Provide array must be same length");
        this.builders = builders;
        int sum = 0;
        minLengths = new int[builders.length];
        for (int i = 0; i < priorities.length; i++) {
            map.computeIfAbsent(priorities[i], k -> new ArrayList<>()).add(i);
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
        int remainder = size - minimalSize;
        if (remainder == 0) return new DistributionResult(minLengths, 0);

        // int[] lengths = Arrays.copyOf(minLengths, builders.length);
        int[] lengths = new int[builders.length];

        for (Integer key : map.keySet()) {
            List<Integer> priorityBuilderIndexes = map.get(key);
            int n = priorityBuilderIndexes.size();
            for (Integer index : priorityBuilderIndexes) {
                lengths[index] = minLengths[index];
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
        return new DistributionResult(lengths, remainder);
    }

    private record DistributionResult(int[] lengths, int remainder) {
    }
}
