package com.ignfab.minalac.generator.parameters.processors.post;

import java.beans.ConstructorProperties;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.processors.post.MetadataFunctionPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for converting distances and altitude into voxel units.
 */
public class MetadataConvertPostProcessorParams extends PostProcessorParams {
    /**
     * Name of metadata to apply conversion (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String metadata;

    /**
     * The type of conversion to perform (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ConversionFunctionParams convertAs;

    /**
     * Policy to apply when the metadata is absent (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public FailurePolicyParams ifMissing = FailurePolicyParams.ERROR;

    /**
     * Policy to apply if conversion fails (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public FailurePolicyParams ifConversionFail = FailurePolicyParams.ERROR;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param metadata the name of the metadata to convert
     * @param convertAs the type of conversion to apply
     */
    @ConstructorProperties({ "metadata", "convertAs" })
    public MetadataConvertPostProcessorParams(String metadata, ConversionFunctionParams convertAs) {
        this.metadata = metadata;
        this.convertAs = convertAs;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (metadata.isBlank())
            throw new IllegalArgumentException("The metadata field cannot be empty or contain only whitespace.");
    }

    @Override
    public PostProcessor<Model, Model> create() {
        return new MetadataFunctionPostProcessor<>(
            metadata,
            (model) -> convertAs.create(model),
            ifMissing.create(),
            ifConversionFail.create()
        );
    }

    /**
     * Type of conversion that be done.
     */
    public enum ConversionFunctionParams {
        /**
         * Converts into voxel unit altitude.
         * No unit conversion is performed.
         * It assumes that the model uses the same unit as the chosen CRS.
         */
        @JsonProperty("altitude")
        ALTITUDE((Model m) -> m.converter()::convertAltitude),
        /**
         * Converts into voxel horizontal distance.
         * No unit conversion is performed.
         * It assumes that the model uses the same unit as the chosen CRS.
         */
        @JsonProperty("horizontalDistance")
        HORIZONTAL_DISTANCE((Model m) -> m.converter()::convertHorizontalDistance),
        /**
         * Converts into voxel vertical distance.
         * No unit conversion is performed.
         * It assumes that the model uses the same unit as the chosen CRS.
         */
        @JsonProperty("verticalDistance")
        VERTICAL_DISTANCE((Model m) -> m.converter()::convertVerticalDistance);

        private final Function<Model, Function<Double, Integer>> provider;

        ConversionFunctionParams(Function<Model, Function<Double, Integer>> provider) {
            this.provider = provider;
        }

        Function<Object, Integer> create(Model model) {
            return (obj) -> {
                if (obj instanceof Number value) {
                    return provider.apply(model).apply(value.doubleValue());
                }
                throw new IllegalArgumentException("Conversion failed: metadata value is not a number");
            };
        }
    }
}
