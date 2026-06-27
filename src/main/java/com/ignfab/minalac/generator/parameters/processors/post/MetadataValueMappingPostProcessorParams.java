package com.ignfab.minalac.generator.parameters.processors.post;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.parameters.ValueParser;
import com.ignfab.minalac.generator.processors.post.MetadataValueMappingPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for {@link MetadataValueMappingPostProcessor}.
 */
public class MetadataValueMappingPostProcessorParams extends PostProcessorParams {
    /**
     * Metadata field to map (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String metadata;

    /**
     * Policy if metadata is missing (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public FailurePolicyParams ifMissing = FailurePolicyParams.ERROR;

    /**
     * Maps each output value to a list of input values (optional, required if 'fromTo' is not set).
     */
    @JsonSetter(contentNulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public Map<String, Set<String>> toFrom;

    /**
     * Maps each input value to a single output value (optional, required if 'toFrom' is not set).
     */
    @JsonSetter(contentNulls = Nulls.FAIL)
    public Map<String, Object> fromTo;

    /**
     * Default value if no match is found (optional).
     */
    @JsonProperty("default")
    public Object defaultValue;

    /**
     * Type of parsed value (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ValueParser<?> as = ValueParser.STRING;

    /**
     * Policy to use when there is no match for the metadata value in the mappings (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public FailurePolicyParams ifNoMatchFound;

    /**
     * Constructor used to ensure that the required fields
     * are present during deserialization.
     *
     * @param metadata name of the metadata to translate
     */
    @ConstructorProperties({ "metadata" })
    public MetadataValueMappingPostProcessorParams(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (metadata.isBlank())
            throw new IllegalArgumentException("The 'metadata' field cannot be empty or contain only whitespace.");
        if (toFrom == null && fromTo == null)
            throw new IllegalArgumentException("Either 'toFrom' or 'fromTo' field must be provided.");
        if (toFrom != null)
            for (Set<String> values : toFrom.values())
                if (values.isEmpty())
                    throw new IllegalArgumentException("The 'from' value cannot be empty.");
        if (ifNoMatchFound != null && defaultValue != null)
            throw new IllegalArgumentException("The 'ifNoMatchFound' field should not be specified when 'default' is set.");
    }

    @Override
    public PostProcessor<Model, Model> create() {
        Map<String, Object> valueMapping = new HashMap<>();
        if (fromTo != null)
            fromTo.forEach((key, value) -> valueMapping.put(key, as.parse(value)));
        if (toFrom != null)
            toFrom.forEach((key, values) -> {
                for (String value : values)
                    if (valueMapping.putIfAbsent(value, as.parse(key)) != null)
                        throw new IllegalArgumentException("Duplicate mapping for value: '%s'".formatted(value));
            });

        Object defaultValue = this.defaultValue;
        if (defaultValue != null)
            defaultValue = as.parse(defaultValue);

        return new MetadataValueMappingPostProcessor<>(
            as.type(),
            metadata,
            valueMapping,
            defaultValue,
            ifMissing.create(),
            Objects.requireNonNullElse(ifNoMatchFound, FailurePolicyParams.ERROR).create()
        );
    }
}
