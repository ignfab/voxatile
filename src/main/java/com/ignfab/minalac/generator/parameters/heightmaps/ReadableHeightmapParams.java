package com.ignfab.minalac.generator.parameters.heightmaps;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.parameters.JsonDelegateDeserialize;

/**
 * Base interface for all {@code ReadableHeightmap} parameters.
 */
@JsonDelegateDeserialize(using = ReadableHeightmapParams.Deserializer.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(ConstantHeightmapParams.class),
    @JsonSubTypes.Type(MultiOperandsHeightmapParams.Sum.class),
    @JsonSubTypes.Type(MultiOperandsHeightmapParams.Product.class),
    @JsonSubTypes.Type(LocalMinimumHeightmapParams.class),
    @JsonSubTypes.Type(CappedManhattanHeightmapParams.class),
    @JsonSubTypes.Type(RemapHeightmapParams.class),
    @JsonSubTypes.Type(StoredHeightmapParams.class)
})
public interface ReadableHeightmapParams {
    /**
     * Validates parameters.
     *
     * @throws IllegalArgumentException if parameters are not valid.
     */
    void validate() throws IllegalArgumentException;

    // TODO: The parameter of this method should be the heightmap store instead of Generation
    //  (See MINALAC-115)
    /**
     * Creates or gets the corresponding {@code ReadableHeightmap}.
     *
     * @param generation the generation context.
     * @return the corresponding heightmap
     */
    ReadableHeightmap create(Generation generation);

    /**
     * Deserializer for {@code ReadableHeightmap}.
     * Deserialization is done in three ways:
     * 1 - If the value is an integer, {@code ConstantHeightmapParams} is used.
     * 2 - If the value is a string, {@code StoredHeightmapParams} is used.
     * 3 - Otherwise, deserialization is done using Jackson deduction mechanism.
     */
    class Deserializer extends DelegatingDeserializer {
        /**
         * Creates a new instance.
         * @param delegate The default deserializer.
         */
        public Deserializer(JsonDeserializer<?> delegate) {
            super(delegate);
        }

        @Override
        protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> delegate) {
            return new Deserializer(delegate);
        }

        @Override
        public Object deserializeWithType(JsonParser jsonParser, DeserializationContext deserializationContext, TypeDeserializer typeDeserializer) throws IOException {
            return switch (jsonParser.currentToken()) {
                case VALUE_NUMBER_INT -> new ConstantHeightmapParams(jsonParser.getIntValue());
                case VALUE_STRING -> new StoredHeightmapParams(jsonParser.getText());
                default -> super.deserializeWithType(jsonParser, deserializationContext, typeDeserializer);
            };
        }
    }
}
