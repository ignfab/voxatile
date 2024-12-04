package com.ignfab.minalac.generator.parameters.renderers.values;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class ModelValueParamsDeserializer extends JsonDeserializer<ModelValueParams<?>> implements ContextualDeserializer {
    private JavaType valueType;

    public ModelValueParamsDeserializer() {
        this.valueType = null;
    }

    private ModelValueParamsDeserializer(JavaType valueType) {
        System.out.println("ModelValueParamsDeserializer new");
        this.valueType = valueType;
    }

    @Override
    public JsonDeserializer<ModelValueParams<?>> createContextual(
        DeserializationContext ctxt, BeanProperty property
    ) throws JsonMappingException {
        return new ModelValueParamsDeserializer(ctxt.getContextualType().containedType(0));
    }

    @SuppressWarnings("rawtypes")
    public ModelValueParams deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JacksonException {

        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        try {
            // Try deserialize value as FixedValue
            @SuppressWarnings("unchecked")
            FixedValueParams fixed = new FixedValueParams(codec.treeToValue(node, valueType.getRawClass()));
            return fixed;
        } catch (MismatchedInputException e) {
            // Could not deserialize value, try as "fromMetadata"
            return codec.treeToValue(node, MetadataValueParams.class);
        }
    }
}