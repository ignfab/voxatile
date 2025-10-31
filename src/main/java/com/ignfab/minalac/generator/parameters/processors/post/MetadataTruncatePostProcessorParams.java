package com.ignfab.minalac.generator.parameters.processors.post;

import java.beans.ConstructorProperties;
import java.util.function.DoubleFunction;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.processors.post.MetadataFunctionPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for truncating values.
 */
public class MetadataTruncatePostProcessorParams extends PostProcessorParams {
    /**
     * Name of metadata to truncate (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String metadata;

    /**
     * Truncation method to apply (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public TruncationMethodParams method;

    /**
     * Policy to apply when the metadata is absent (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public FailurePolicyParams ifMissing = FailurePolicyParams.ERROR;

    /**
     * Policy to apply if truncation fails (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public FailurePolicyParams ifTruncationFail = FailurePolicyParams.ERROR;

    /**
     * Constructor used to ensure that the required fields
     * are present during deserialization.
     *
     * @param metadata metadata name to truncate
     * @param method truncation method to apply
     */
    @ConstructorProperties({ "metadata", "method" })
    public MetadataTruncatePostProcessorParams(String metadata, TruncationMethodParams method) {
        this.metadata = metadata;
        this.method = method;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (metadata.isBlank())
            throw new IllegalArgumentException("The 'metadata' field cannot be empty or contain only whitespace.");
    }

    @Override
    public PostProcessor<Model, Model> create() {
        return new MetadataFunctionPostProcessor<>(
            metadata,
            (model) -> method.create(),
            ifMissing.create(),
            ifTruncationFail.create()
        );
    }

    /**
     * Truncation methods that can be applied to numeric values.
     */
    public enum TruncationMethodParams {
        /**
         * Round to nearest integer.
         */
        @JsonProperty("round")
        ROUND(Math::round),
        /**
         * Round up to next integer.
         */
        @JsonProperty("ceil")
        CEIL(Math::ceil),
        /**
         * Round down to previous integer.
         */
        @JsonProperty("floor")
        FLOOR(Math::floor);

        private final DoubleFunction<Number> method;

        TruncationMethodParams(DoubleFunction<Number> method) {
            this.method = method;
        }

        /**
         * {@return the associated method}
         */
        public Function<Object, Integer> create() {
            return obj -> {
                if (obj instanceof Number number)
                    return method.apply(number.doubleValue()).intValue();
                throw new IllegalArgumentException("Truncation failed: metadata value is not a number.");
            };
        }
    }
}
