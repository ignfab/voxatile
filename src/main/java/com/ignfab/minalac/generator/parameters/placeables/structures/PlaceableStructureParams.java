package com.ignfab.minalac.generator.parameters.placeables.structures;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Main parameter class for {@link PlaceableStructure}.
 * <p>
 * Structure can be described using one of PlaceableStructureParams.Variant subclass or a combination of several.
 */
@JsonDeserialize(using = PlaceableStructureParams.Deserializer.class)
public final class PlaceableStructureParams extends PlaceableParams {

    private final List<Variant> params;

    private PlaceableStructureParams(List<Variant> params) {
        this.params = params;
    }

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
     * Custom deserializer creating a PlaceableStructureParams out of a structure list or single structure.
     */
    public static class Deserializer extends ValueDeserializer<PlaceableStructureParams> {

        @Override
        public PlaceableStructureParams deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            JsonNode node = parser.readValueAsTree();

            // Decode array into structure list
            if (node.isArray()) {
                List<Variant> list = new LinkedList<>();
                for (JsonNode item : node)
                    list.add(context.readTreeAsValue(item, Variant.class));

                return new PlaceableStructureParams(list);
            }

            // Decode single structure into a single value list
            if (node.isObject()) {
                Variant structure = context.readTreeAsValue(node, Variant.class);
                return new PlaceableStructureParams(List.of(structure));
            }

            throw new InputCoercionException(parser, "Structure should be either a list or an single structure", node.asToken(), PlaceableStructureParams.class);
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
         * <p>
         * It is used rather than a {@code create} method to allow merging several parameters into one structure.
         *
         * @param seed Random seed to use for this {@code PlaceableStructure}.
         * @param structureBuilder Structure builder to put created placeables into
         */
        public abstract void apply(Seed seed, PlaceableStructure.Builder structureBuilder);
    }
}
