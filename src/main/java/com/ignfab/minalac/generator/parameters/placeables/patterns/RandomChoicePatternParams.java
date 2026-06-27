package com.ignfab.minalac.generator.parameters.placeables.patterns;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Pattern;
import com.ignfab.minalac.generator.placeables.RandomChoicePattern;
import com.ignfab.minalac.generator.placeables.RandomPattern;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for {@link RandomPattern} placeable.
 */
public class RandomChoicePatternParams extends PatternParams {
    /**
     * List of possible choices.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public List<Choice> randomChoice;

    /**
     * Random seed.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String seed = "";

    /**
     * Creates a new {@code RandomPatternParams}.
     * @param randomChoice list of possible choices
     */
    @ConstructorProperties("randomChoice")
    public RandomChoicePatternParams(List<Choice> randomChoice) {
        this.randomChoice = randomChoice;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        randomChoice.forEach(Choice::validate);
    }

    @Override
    public Pattern create(Seed seed) {
        return new RandomChoicePattern(
            randomChoice.stream().map(choice -> choice.create(seed)).toList(),
            seed.salt(this.seed)
        );
    }

    public static class Choice {
        /**
         * What to place.
         */
        @JsonSetter(nulls = Nulls.FAIL)
        public PlaceableParams place;

        /**
         * Weight of the element from the pool (default 1).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public double weight = 1;

        /**
         * Creates a new {@code Choice}.
         * @param place what to place
         */
        @ConstructorProperties("place")
        public Choice(PlaceableParams place) {
            this.place = place;
        }

        /**
         * Validates parameters.
         * @throws IllegalArgumentException if parameter is invalid
         */
        public void validate() throws IllegalArgumentException {
            place.validate();
            if (weight <= 0)
                throw new IllegalArgumentException("Weight must be greater than zero");
        }

        /**
         * Creates the corresponding choice object.
         * @param seed random seed to use
         * @return the created choice
         */
        public RandomChoicePattern.Choice create(Seed seed) {
            return new RandomChoicePattern.Choice(place.create(seed), weight);
        }
    }
}
