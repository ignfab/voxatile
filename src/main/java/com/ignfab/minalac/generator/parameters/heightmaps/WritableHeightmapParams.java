package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmapSpec;
import com.ignfab.minalac.generator.parameters.JsonDelegateDeserialize;
import com.ignfab.minalac.generator.parameters.utils.StringNotBlank;

/**
 * Parameter class for a {@link WritableHeightmapSpec}.
 */
@JsonDelegateDeserialize(using = WritableHeightmapParams.Deserializer.class)
public class WritableHeightmapParams implements ReadableHeightmapParams {
    /**
     * The name of the heightmap.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public StringNotBlank stored;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param stored the name of the heightmap.
     */
    @ConstructorProperties("stored")
    public WritableHeightmapParams(StringNotBlank stored) {
        this.stored = stored;
    }

    @Override
    public WritableHeightmapSpec create(HeightmapDeclarationStore store) {
        return store.get(stored.create()).spec();
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
                case VALUE_STRING -> new WritableHeightmapParams(jsonParser.readValueAs(StringNotBlank.class));
                default -> super.deserializeWithType(jsonParser, deserializationContext, typeDeserializer);
            };
        }
    }
}
