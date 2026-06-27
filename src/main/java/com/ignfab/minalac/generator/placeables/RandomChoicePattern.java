package com.ignfab.minalac.generator.placeables;

import java.util.List;

import com.ignfab.minalac.generator.utils.random.Random;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A pattern choosing something to place from a weighted pool.
 */
public class RandomChoicePattern implements Pattern {
    private final List<Choice> choices;
    private final double totalWeight;
    private final Random random;

    /**
     * Creates a new {@code RandomChoicePattern}.
     *
     * @param choices Choices to randomly select from
     * @param seed {@link Seed} to use for random number generation
     */
    public RandomChoicePattern(List<Choice> choices, Seed seed) {
        this.choices = List.copyOf(choices);
        totalWeight = choices.stream().mapToDouble(Choice::weight).sum();
        random = seed.createRandom();
    }

    @Override
    public Placeable get(VoxelTile tile, int x, int y, int z) {
        random.setSeed(x, y, z);
        double r = random.nextDouble(totalWeight);
        for (Choice choice : choices) {
            r -= choice.weight();
            if (r < 0)
                return choice.placeable();
        }
        return Nothing.INSTANCE;
    }

    /**
     * An entry from the pool.
     * @param placeable Placeable to place
     * @param weight Weight of the entry
     */
    public record Choice(Placeable placeable, double weight) {}
}
