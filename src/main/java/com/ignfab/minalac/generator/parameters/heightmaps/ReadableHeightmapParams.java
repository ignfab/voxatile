package com.ignfab.minalac.generator.parameters.heightmaps;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.DelegatingDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.parameters.JsonDelegateDeserialize;

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
public interface ReadableHeightmapParams {
    /**
     * Validates parameters.
     *
     * @throws IllegalArgumentException if parameters are not valid.
     */
    void validate() throws IllegalArgumentException;

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
     * <ol>
     *  <li>If the value is an integer, {@code ConstantHeightmapParams} is used.</li>
     *  <li>If the value is a string, {@code StoredHeightmapParams} is used.</li>
     *  <li>Otherwise, deserialization is done using Jackson deduction mechanism.</li>
     * </ol>
     */
    class Deserializer extends DelegatingDeserializer {
        /**
         * Creates a new instance.
         * @param delegate The default deserializer.
         */
        public Deserializer(ValueDeserializer<?> delegate) {
            super(delegate);
        }

        @Override
        protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> delegate) {
            return new Deserializer(delegate);
        }

        @Override
        public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer) {
            return switch (parser.currentToken()) {
                case VALUE_NUMBER_INT -> new ConstantHeightmapParams(parser.getIntValue());
                case VALUE_STRING -> new WritableHeightmapParams(parser.getString());
                default -> super.deserializeWithType(parser, context, typeDeserializer);
            };
        }
    }
}
