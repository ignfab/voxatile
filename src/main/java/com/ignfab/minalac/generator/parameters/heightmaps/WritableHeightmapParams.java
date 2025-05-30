package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

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
        public Deserializer(JsonDeserializer<?> delegate) {
            super(delegate);
        }

        @Override
        protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> delegate) {
            return new Deserializer(delegate);
        }

        @Override
        public Object deserializeWithType(JsonParser jsonParser, DeserializationContext deserializationContext, TypeDeserializer typeDeserializer) throws IOException {
            if (jsonParser.currentToken() == JsonToken.VALUE_STRING)
                return new WritableHeightmapParams(jsonParser.getText());
            return super.deserializeWithType(jsonParser, deserializationContext, typeDeserializer);
        }
    }
}
