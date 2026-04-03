package com.ignfab.minalac.generator.parameters.placeables.structures;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.JsonWrapper;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Main parameter class for {@link PlaceableStructure}.
 * <p>
 * Structure can be described using one of PlaceableStructureParams.Variant subclass or a combination of several.
 */
@JsonWrapper
public final class PlaceableStructureParams extends PlaceableParams {

    /**
     * Structure contents.
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<Variant> params;

    @Override
    public void validate() {
        // Only validation propagation
        for (Variant param : params)
            param.validate();
    }

    @Override
    public PlaceableStructure create(Seed seed) {
        PlaceableStructure.Builder structureBuilder = PlaceableStructure.builder();
        for (Variant param : params)
            param.apply(seed, structureBuilder);

        return structureBuilder.build();
    }

    /**
     * Abstract class for all structure parameters variants.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({
        @JsonSubTypes.Type(BlueprintPlaceableStructureParams.class),
        @JsonSubTypes.Type(BoxPlaceableStructureParams.class),
    })
    public abstract static class Variant {
        /**
         * Validates parameters.
         */
        public void validate() {}

        /**
         * Applies this parameters content to the given structure.
         * <p>
         * It is used rather than a {@code create} method to allow merging several parameters into one structure.
         *
         * @param seed Random seed to use for this {@code PlaceableStructure}.
         * @param structureBuilder Structure builder to put created placeables into
         */
        public abstract void apply(Seed seed, PlaceableStructure.Builder structureBuilder);
    }
}
