package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.SizesAxisMapper;

/**
 * An {@link AxisMapperBuilder} that distributes space to undelying {@link AxisMapperBuilder} with priorities.
 * <p>
 * Origins of {@link AxisMapperBuilder} are ignored as they are placed according to {@code PriorityRepartitionAxisMapperBuilder} computations.
 */
public class PriorityRepartitionAxisMapperBuilder implements AxisMapperBuilder {
    private final AxisMapperBuilder[] builders;
    private final TreeMap<Integer, List<Integer>> priorities = new TreeMap<>(Collections.reverseOrder());
    private final int[] minLengths;
    private final int minimalSize;

    /**
     * Creates a new {@code PriorityRepartitionAxisMapperBuilder}.
     * <p>
     * Underlying builders will be placed side by side. To fill total size, they will be resized according to priorities.
     * Higher priority gets extra size first. If they can't fill the size, remaining will be given to lower priorities.
     *
     * @param builders underlying builders, must have the same lenght as {@code priorities}
     * @param priorities corresponding priorities, must have the same lenght as {@code builders}
     * @throws UnbuildableException if underlying builders are not adjustable
     */
    public PriorityRepartitionAxisMapperBuilder(AxisMapperBuilder[] builders, int[] priorities) throws UnbuildableException {
        if (builders.length != priorities.length)
            throw new IllegalArgumentException("Provide array must be same length");

        int sum = 0;
        minLengths = new int[builders.length];
        this.builders = builders;

        for (int i = 0; i < builders.length; i++) {
            // Force underlying builders to fill underlying space
            builders[i].makeAdjusted();
            // Sort builder indexes by priorities
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
        int[] lengths = minLengths.clone();

        // Now we distribute remaining by prirority order
        for (Map.Entry<Integer, List<Integer>> priority : priorities.entrySet()) {
            List<Integer> candidates = new ArrayList<>(priority.getValue());

            // Process all candidates with the same priority
            while (remaining > 0 && candidates.size() > 0) {
                // Starts with smallest candidate
                candidates.sort(Comparator.comparingInt((value) -> lengths[value]));

                int lastRemaining = remaining;
                int count = candidates.size();
                ListIterator<Integer> iter = candidates.listIterator();
                while (iter.hasNext()) {
                    int index = iter.next();

                    int possible = builders[index].maxSizeUnder(lengths[index] + Math.ceilDiv(remaining, count));

                    if (possible > lengths[index]) {
                        // Ok, candidates takes what we gave to him
                        remaining += lengths[index] - possible;
                        lengths[index] = possible;
                    } else {
                        // We can suspect this candidate is out of the race
                        possible = builders[index].maxSizeUnder(lengths[index] + remaining);
                        if (possible == lengths[index])
                            // This one won't take anymore
                            iter.remove();
                    }
                    count--;
                }

                // No progress... give up
                if (remaining == lastRemaining)
                    break;
                // Job done !
                if (remaining == 0)
                    break;
            }
        }
        return new DistributionResult(lengths, remaining);
    }

    private record DistributionResult(int[] lengths, int remainder) {
    }

    @Override
    public int origin() {
        return 0;
    }
}
