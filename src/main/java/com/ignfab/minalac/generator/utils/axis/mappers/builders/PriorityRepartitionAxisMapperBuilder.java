package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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
     * @param builders   underlying builders, must have the same length as {@code priorities}
     * @param priorities corresponding priorities, must have the same length as {@code builders}
     * @throws UnbuildableException if underlying builders are not adjustable
     */
    public PriorityRepartitionAxisMapperBuilder(AxisMapperBuilder[] builders, int[] priorities) throws UnbuildableException {
        if (builders.length != priorities.length)
            throw new IllegalArgumentException("Provide array must be same length");

        int sum = 0;
        minLengths = new int[builders.length];
        this.builders = builders;

        for (int i = 0; i < builders.length; i++) {
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
        int remainder = compute(size).remainder;
        return size - remainder;
    }

    @Override
    public int minimumSize() {
        return minimalSize;
    }

    private DistributionResult compute(int size) {
        int remaining = size - minimalSize;
        if (remaining == 0) return new DistributionResult(minLengths, 0);
        int[] lengths = minLengths.clone();

        // Distribute remaining by priority group
        for (Map.Entry<Integer, List<Integer>> priority : priorities.entrySet()) {
            List<Integer> candidates = new ArrayList<>(priority.getValue());
            boolean firstIteration = true;

            // There are two phases.
            // Phase 1 : We try to feed each builder with a fairShare between the candidates
            // Phase 2 : A starved builder a builder who hasn't eaten but can eat.  This phase give each starved successively larger share until one or more eat.
            while (remaining > 0 && !candidates.isEmpty()) {
                int lastRemaining = remaining;
                int lastCount = candidates.size();
                candidates.sort(Comparator.comparingInt(i -> lengths[i]));
                // Ceil division
                int fairShare = (remaining + candidates.size() - 1) / candidates.size();

                List<Integer> starved = new ArrayList<>();

                // This is phase 1 : Fast distribution
                Iterator<Integer> candidateIterator = candidates.iterator();
                while (candidateIterator.hasNext()) {
                    int index = candidateIterator.next();
                    // Builders with a minimumSize already ate during allocation.
                    // First iteration is different for them to prevent potential starvation of other candidates.
                    if (firstIteration && lengths[index] > 0) {
                        int toEat = builders[index].maxSizeUnder(fairShare);
                        // Should eat if its allocated minimal size is inferior to what it could have eaten with no min size.
                        if (toEat > lengths[index]) {
                            remaining = remaining - (toEat - lengths[index]);
                            lengths[index] = toEat;
                        }
                        continue;
                    }
                    int toEat = builders[index].maxSizeUnder(lengths[index] + Math.min(remaining, fairShare));
                    if (toEat > lengths[index]) {
                        remaining = remaining - (toEat - lengths[index]);
                        lengths[index] = toEat;
                    // Can not eat, it is no longer a candidate
                    } else if (builders[index].maxSizeUnder(lengths[index] + remaining) == lengths[index]) {
                        candidateIterator.remove();
                    // Can potentially eat, will go on phase 2
                    } else {
                        starved.add(index);
                    }
                }

                // Phase 2 give each starved successively larger share until one or more eat.
                // All candidates that end up on starved list have the possibility to eat something (Otherwise they would have been removed on phase 1).
                // They may no all eat, but at least one starved will eat.
                while (!starved.isEmpty() && remaining > 0) {
                    int lastStarvedRemaining = remaining;
                    starved.sort(Comparator.comparingInt(i -> lengths[i]));
                    for (int count = lastCount - 1; count > 0; count--) {
                        int share = (remaining + count - 1) / count;

                        Iterator<Integer> starvedIterator = starved.iterator();
                        while (starvedIterator.hasNext()) {
                            int currentIndex = starvedIterator.next();
                            int toEat = builders[currentIndex].maxSizeUnder(lengths[currentIndex] + Math.min(remaining, share));

                            if (toEat > lengths[currentIndex]) {
                                remaining = remaining - (toEat - lengths[currentIndex]);
                                lengths[currentIndex] = toEat;
                                starvedIterator.remove();
                                break;
                            }
                        }
                    }

                    // We give up phase 2. Full candidates will be removed on phase 1.
                    if (lastStarvedRemaining == remaining)
                        break;
                }

                // No progress at all we give up. It should not be necessary.

                // At the end of phase 1. Candidate is either : full (and removed), hasEaten or isStarved (canEat and on starvedList).
                // Worst scenario of no progress is : all in starvedList.
                // On phase 2, each starved candidate is given successive larger share until reaching remaining.
                // Worst scenario is that only one among the starved eat.
                // If others starved can't eat phase 2 will break and those starved will be eventually be removed.

                // Since I can't be 100% sure on edges cases, I have put this test as a safeguard.
                if (lastRemaining == remaining && lastCount == candidates.size() && !firstIteration)
                    break;

                firstIteration = false;
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
