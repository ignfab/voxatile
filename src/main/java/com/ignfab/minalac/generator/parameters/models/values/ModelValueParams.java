package com.ignfab.minalac.generator.parameters.models.values;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.DelegatingDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.AbsentValue;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.parameters.JsonDelegateDeserialize;

/**
 * Base interface for all {@link ModelValue} parameters.
 */
@JsonDelegateDeserialize(using = ModelValueParams.Deserializer.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(FixedValueParams.class),
    @JsonSubTypes.Type(MetadataValueParams.class),
    @JsonSubTypes.Type(SingleOperandModelValueParams.Round.class),
    @JsonSubTypes.Type(SingleOperandModelValueParams.Floor.class),
    @JsonSubTypes.Type(SingleOperandModelValueParams.Ceil.class),
    @JsonSubTypes.Type(MultiOperandsModelValueParams.Sum.class),
    @JsonSubTypes.Type(MultiOperandsModelValueParams.Product.class),
    @JsonSubTypes.Type(MultiOperandsModelValueParams.Lowest.class),
    @JsonSubTypes.Type(MultiOperandsModelValueParams.Highest.class),
    @JsonSubTypes.Type(FallbackModelValueParams.class),
    @JsonSubTypes.Type(RandomUniformModelValueParams.class),
    @JsonSubTypes.Type(InverseModelValueParams.class),
    @JsonSubTypes.Type(ConditionalModelValueParams.class)
})
public abstract class ModelValueParams {
    /**
     * Validates parameters.
     * @throws IllegalArgumentException if parameters are not valid.
     */
    public void validate() {}

    /**
     * Creates the corresponding {@link ModelValue} for that generation.
     * @param generation the generation context
     * @return the corresponding model value
     */
    public abstract ModelValue create(Generation generation);

    /**
     * Deserializer for {@code ModelValueParams}.
     * Deserialization is done in three ways:
     * <ol>
     *  <li>If the value is a number, {@link FixedValueParams} is used.</li>
     *  <li>If the value is a string:
     *   <ol type="a">
     *    <li>If the string value is "{@code absent}", {@link AbsentValue} is used.</li>
     *    <li>Otherwise, {@link MetadataValueParams} is used.</li>
     *   </ol>
     *  </li>
     *  <li>Otherwise, deserialization is done using Jackson deduction mechanism.</li>
     * </ol>
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
                case VALUE_NUMBER_INT, VALUE_NUMBER_FLOAT -> new FixedValueParams(parser.getDoubleValue());
                case VALUE_STRING -> {
                    String str = parser.getString();
                    yield str.equals("absent") ? new AbsentValueParams() : new MetadataValueParams(str);
                }
                default -> super.deserializeWithType(parser, context, typeDeserializer);
            };
        }
    }
}
