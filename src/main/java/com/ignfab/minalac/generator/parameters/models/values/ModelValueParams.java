package com.ignfab.minalac.generator.parameters.models.values;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.models.values.RandomUniformModelValue;
import com.ignfab.minalac.generator.parameters.JsonDelegateDeserialize;

@JsonDelegateDeserialize(using = ModelValueParams.Deserializer.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(FixedValueParams.class),
    @JsonSubTypes.Type(MultiOperandsModelValueParams.Sum.class),
    @JsonSubTypes.Type(MultiOperandsModelValueParams.Product.class),
    @JsonSubTypes.Type(FallbackModelValueParams.class),
    @JsonSubTypes.Type(RandomUniformModelValue.class),
    @JsonSubTypes.Type(SingleOperandModelValueParams.Round.class),
    @JsonSubTypes.Type(SingleOperandModelValueParams.Floor.class),
    @JsonSubTypes.Type(SingleOperandModelValueParams.Ceil.class),
    @JsonSubTypes.Type(MetadataValueParams.class)
})
public abstract class ModelValueParams {
    public void validate() {}

    public abstract ModelValue create(Generation generation);

    public static class Deserializer extends DelegatingDeserializer {
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
                case VALUE_NUMBER_INT, VALUE_NUMBER_FLOAT -> new FixedValueParams(jsonParser.getDoubleValue());
                case VALUE_STRING -> new MetadataValueParams(jsonParser.getText());
                default -> super.deserializeWithType(jsonParser, deserializationContext, typeDeserializer);
            };
        }
    }
}
