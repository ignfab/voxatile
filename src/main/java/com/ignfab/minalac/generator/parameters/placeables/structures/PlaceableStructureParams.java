package com.ignfab.minalac.generator.parameters.placeables.structures;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * Main parameter class for {@link PlaceableStructure}.
 * <p>
 * Structure can be described using one of PlaceableStructureParams.Variant subclass or a combination of several.
 */
@JsonDeserialize(using = PlaceableStructureParams.Deserializer.class)
public class PlaceableStructureParams extends PlaceableParams {

    private List<Variant> params;

    private PlaceableStructureParams(List<Variant> params) {
        this.params = params;
    }

    // Empty constructor for inheritance purposes.
    protected PlaceableStructureParams() {}

    @Override
    public void validate() {
        // Only validation propagation
        for (Variant param : params)
            param.validate();
    }

    @Override
    public PlaceableStructure create(Seed seed) {
        return new PlaceableStructure(createPlaceables(seed));
    }

    // TODO-PR: They should be a way to do it better - Needed for ElasticPlaceableStructureParams
    /**
     * Creates placeables map for the structure.
     *
     * @param seed Random seed to use for this {@code Placeable}.
     * @return placeables map
     */
    public Map<WorldCoords3d, Placeable> createPlaceables(Seed seed) {
        Map<WorldCoords3d, Placeable>  structure = new HashMap<>();
        for (Variant param : params)
            param.apply(seed, structure);
        return structure;
    }

    /**
     * Custom deserializer creating a PlaceableStructureParams out of a structure list or single structure.
     */
    public static class Deserializer extends JsonDeserializer<PlaceableStructureParams> {

        @Override
        public PlaceableStructureParams deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JacksonException {

            ObjectCodec codec = jp.getCodec();
            JsonNode node = codec.readTree(jp);

            // Decode array into structure list
            if (node.isArray()) {
                List<Variant> list = new LinkedList<>();
                for (JsonNode item : node)
                    list.add(codec.treeToValue(item, Variant.class));

                return new PlaceableStructureParams(list);
            }

            // Decode single structure into a single value list
            if (node.isObject()) {
                Variant structure = codec.treeToValue(node, Variant.class);
                return new PlaceableStructureParams(List.of(structure));
            }

            throw new InputCoercionException(jp, "Structure should be either a list or an single structure", node.asToken(), PlaceableStructureParams.class);
        }
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
         *
         * It is used rather than a {@code create} method to allow merging several parameters into one structure.
         *
         * @param seed Random seed to use for this {@code PlaceableStructure}.
         * @param placeables Structure which put created voxels into
         */
        public abstract void apply(Seed seed, Map<WorldCoords3d, Placeable> placeables);
    }
}
