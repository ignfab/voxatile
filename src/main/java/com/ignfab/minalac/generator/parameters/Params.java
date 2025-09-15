package com.ignfab.minalac.generator.parameters;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

public interface Params {
    default void validate() throws IllegalArgumentException {}

    class Deserializer extends DelegatingDeserializer {
        public Deserializer(JsonDeserializer<?> delegate) {
            super(delegate);
        }

        @Override
        protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> delegate) {
            return new Deserializer(delegate);
        }

        @Override
        public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer) throws IOException {
            JsonLocation location = parser.currentTokenLocation();
            Object object = super.deserializeWithType(parser, context, typeDeserializer);
            postDeserialize(object, location);
            return object;
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonLocation location = parser.currentTokenLocation();
            Object object = super.deserialize(parser, context);
            postDeserialize(object, location);
            return object;
        }

        private void postDeserialize(Object object, JsonLocation location) throws IOException {
            if (object instanceof Params params) {
                try {
                    params.validate();
                } catch (IllegalArgumentException e) {
                    throw new IOException("Unable to validate params: " + location, e);
                }
            }
        }
    }

    class BeanModifier extends BeanDeserializerModifier {
        @Override
        public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
            return Params.class.isAssignableFrom(beanDesc.getBeanClass()) ? new Deserializer(deserializer) : deserializer;
        }
    }
}
