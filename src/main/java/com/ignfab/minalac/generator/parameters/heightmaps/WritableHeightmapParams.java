package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.DelegatingDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmapSpec;
import com.ignfab.minalac.generator.parameters.JsonDelegateDeserialize;

/**
 * Parameter class for a {@link WritableHeightmapSpec}.
 */
@JsonDelegateDeserialize(using = WritableHeightmapParams.Deserializer.class)
public class WritableHeightmapParams implements ReadableHeightmapParams {
    /**
     * The name of the heightmap.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String stored;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param stored the name of the heightmap.
     */
    @ConstructorProperties("stored")
    public WritableHeightmapParams(String stored) {
        this.stored = stored;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (stored.isBlank())
            throw new IllegalArgumentException("Name cannot be empty or blank");
    }

    @Override
    public WritableHeightmapSpec create(HeightmapDeclarationStore store) {
        return store.get(stored).spec();
    }

    /**
     * Deserializer for {@code WritableHeightmapParams}.
     * If the value is a string, it is used as name, otherwise, deserialization is done using Jackson deduction mechanism.
     */
    public static class Deserializer extends DelegatingDeserializer {
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
                case VALUE_STRING -> new WritableHeightmapParams(parser.getString());
                default -> super.deserializeWithType(parser, context, typeDeserializer);
            };
        }
    }
}
