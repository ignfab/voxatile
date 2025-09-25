package com.ignfab.minalac.generator.parameters.placeables.patterns;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.ConditionalPlaceable;
import com.ignfab.minalac.generator.placeables.Pattern;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

public class InsteadPatternParams extends PatternParams {
    /**
     * What to place.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public final PlaceableParams place;

    /**
     * What to replace only.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public final List<PlaceableParams> onlyInsteadOf = Collections.emptyList();

    /**
     * What to preserve.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public final List<PlaceableParams> neverInsteadOf = Collections.emptyList();;

    @ConstructorProperties({"place"})
    public InsteadPatternParams(PlaceableParams place) {
        this.place = place;
    }

    @Override
    public void validate() {
        if (!(onlyInsteadOf.isEmpty() ^ neverInsteadOf.isEmpty()))
            throw new IllegalArgumentException("One an only one of onlyInsteadOf and neverInsteadOf fields must be specified and non empty");

        place.validate();

        onlyInsteadOf.forEach(PlaceableParams::validate);
        neverInsteadOf.forEach(PlaceableParams::validate);
    }

    @Override
    public Pattern create(Seed seed) {
        Placeable placeable = place.create(seed);

        if (!onlyInsteadOf.isEmpty()) {
            List<Placeable> list = onlyInsteadOf.stream().map(p -> p.create(seed)).collect(Collectors.toList());
            return new ConditionalPlaceable(
                placeable,
                (tile, x, y, z) -> list.contains(tile.getVoxel(x, y, z))
            );
        }

        if (!neverInsteadOf.isEmpty()) {
            List<Placeable> list = neverInsteadOf.stream().map(p -> p.create(seed)).collect(Collectors.toList());
            return new ConditionalPlaceable(
                placeable,
                (tile, x, y, z) -> !list.contains(tile.getVoxel(x, y, z))
            );
        }

        throw new IllegalStateException("How did I get there?");
    }
}
