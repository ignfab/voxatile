package com.ignfab.minalac.generator.parameters.processors.post;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.parameters.models.filters.ModelFilterParams;
import com.ignfab.minalac.generator.processors.post.ConditionalPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for {@link ConditionalPostProcessor}.
 */
public class ConditionalPostProcessorParams extends PostProcessorParams {
    /**
     * Filter to decide which models should be post-processed (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    @JsonProperty("if")
    public ModelFilterParams condition;

    /**
     * Post-processor to apply to matching models (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    @JsonProperty("then")
    public PostProcessorParams postProcessorIfTrue;

    /**
     * Post-processor to apply to other models (optional, defaults to identity).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("else")
    public PostProcessorParams postProcessorIfFalse = new IdentityPostProcessorParams();

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     * @param condition the filter to match models
     * @param postProcessorIfTrue the post-processor to apply
     */
    @ConstructorProperties({ "condition", "postProcessorIfTrue" })
    public ConditionalPostProcessorParams(ModelFilterParams condition, PostProcessorParams postProcessorIfTrue) {
        this.condition = condition;
        this.postProcessorIfTrue = postProcessorIfTrue;
    }

    @Override
    @SuppressWarnings("unchecked") // Types will be validated later
    public PostProcessor<?, ?> create() {
        return new ConditionalPostProcessor<>(
            condition.create(),
            (PostProcessor<Model, ?>) postProcessorIfTrue.create(),
            (PostProcessor<Model, ?>) postProcessorIfFalse.create()
        );
    }
}
