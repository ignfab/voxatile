package com.ignfab.minalac.generator.parameters.heightmaps;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.parameters.JsonDelegateDeserialize;
import com.ignfab.minalac.generator.parameters.Params;
import com.ignfab.minalac.generator.parameters.utils.StringNotBlank;

/**
 * Base interface for all {@code ReadableHeightmapSpec} parameters.
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
    @JsonSubTypes.Type(WritableHeightmapParams.class)
})
public interface ReadableHeightmapParams extends Params {
    /**
     * Creates the corresponding {@code ReadableHeightmap} eventually using stored heightmap declarations from given store.
     *
     * @param store the stored heightmap spec store to use to get subsequent heightmap declarations
     * @return the corresponding heightmap spec
     */
    ReadableHeightmapSpec create(HeightmapDeclarationStore store);

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
                case VALUE_STRING -> new WritableHeightmapParams(jsonParser.readValueAs(StringNotBlank.class));
                default -> super.deserializeWithType(jsonParser, deserializationContext, typeDeserializer);
            };
        }
    }
}
