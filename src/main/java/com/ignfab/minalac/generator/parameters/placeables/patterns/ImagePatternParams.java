package com.ignfab.minalac.generator.parameters.placeables.patterns;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.ImagePattern;
import com.ignfab.minalac.generator.placeables.Pattern;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.Color;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters an {@link ImagePattern} placeable.
 */
public class ImagePatternParams extends PatternParams {

    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    public Map<String, PlaceableParams> place;

    /**
     * Creates a new {@code ImagePatternParams}.
     *
     * @param models models from which get colors (should be images)
     * @param place a map of color strings associated with what to place
     */
    @ConstructorProperties({"models", "place"})
    public ImagePatternParams(ModelSelectionParams models, Map<String, PlaceableParams> place) {
        this.models = models;
        this.place = place;
    }

    @Override
    public void validate() {
        models.validate();

        for (Map.Entry<String, PlaceableParams> entry : place.entrySet()) {
            Color.fromString(entry.getKey()); // We will decode colors twice but this is fast
            entry.getValue().validate();
        }
    }

    @Override
    public Pattern create(Seed seed) {

        Map<Color, Placeable> placeables = new HashMap<>();

        for (Map.Entry<String, PlaceableParams> entry : place.entrySet())
            placeables.put(Color.fromString(entry.getKey()),entry.getValue().create(seed));

        return new ImagePattern(models.create(), placeables);
    }
}
