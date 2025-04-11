package com.ignfab.minalac.generator.parameters.heightmaps;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ignfab.minalac.generator.generation.Store;
import com.ignfab.minalac.generator.generation.heightmaps.UnboundHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.UnboundReadableHeightmap;

/**
 * Base interface for all {@code ReadableHeightmap} parameters.
 */
@JsonDeserialize(using = ReadableHeightmapParams.Deserializer.class)
public interface ReadableHeightmapParams {
    /**
     * Validates parameters.
     *
     * @throws IllegalArgumentException if parameters are not valid.
     */
    void validate() throws IllegalArgumentException;

    /**
     * Creates or gets the corresponding {@code ReadableHeightmap}.
     *
     * @param store the unbounded heightmap store to use to get subsequent heightmaps
     * @return the corresponding heightmap
     */
    UnboundReadableHeightmap create(Store<UnboundHeightmap> store);

    /**
     * Deserializer for {@code ReadableHeightmap}.
     * Deserialization is done in three ways:
     * 1 - If the value is an integer, {@code ConstantHeightmapParams} is used.
     * 2 - If the value is a string, {@code StoredHeightmapParams} is used.
     * 3 - Otherwise, deserialization is done using Jackson deduction mechanism.
     */
    class Deserializer extends JsonDeserializer<ReadableHeightmapParams> {
        @Override
        public ReadableHeightmapParams deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            ObjectCodec codec = jsonParser.getCodec();
            JsonNode node = codec.readTree(jsonParser);
            if (node.isInt())
                return new ConstantHeightmapParams(node.asInt());
            if (node.isTextual())
                return new StoredHeightmapParams(node.asText());
            if (node.isObject())
                return codec.treeToValue(node, CustomReadableHeightmapParams.class);
            throw new InputCoercionException(jsonParser, "ReadableHeightmap should be either an integer, a string or an object", node.asToken(), ReadableHeightmapParams.class);
        }
    }
}
