package com.ignfab.minalac.generator.parameters.models.values;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.ConditionalModelValue;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.parameters.models.filters.ModelFilterParams;

/**
 * Parameters for {@link com.ignfab.minalac.generator.models.values.ConditionalModelValue}.
 */
public class ConditionalModelValueParams extends ModelValueParams {
    /**
     * Filter to decide which model value to return (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    @JsonProperty("if")
    public ModelFilterParams condition;

    /**
     * Model value to return for matching models (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    @JsonProperty("then")
    public ModelValueParams valueIfTrue;

    /**
     * Model value to return for other models (optional, defaults to absent).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("else")
    public ModelValueParams valueIfFalse = new AbsentValueParams();

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     * @param condition the filter to match models
     * @param valueIfTrue the model value to return
     */
    @ConstructorProperties({ "condition", "valueIfTrue" })
    public ConditionalModelValueParams(ModelFilterParams condition, ModelValueParams valueIfTrue) {
        this.condition = condition;
        this.valueIfTrue = valueIfTrue;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        condition.validate();
        valueIfTrue.validate();
        valueIfFalse.validate();
    }

    @Override
    public ModelValue create(Generation generation) {
        return new ConditionalModelValue(
            condition.create(generation),
            valueIfTrue.create(generation),
            valueIfFalse.create(generation)
        );
    }
}
