package com.ignfab.minalac.generator.parameters.models.values;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.models.values.RandomUniformModelValue;

public class RandomUniformModelValueParams extends ModelValueParams {
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams min;

    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams max;

    @JsonSetter(nulls = Nulls.SKIP)
    public String seed = "";

    @ConstructorProperties({ "min", "max" })
    public RandomUniformModelValueParams(ModelValueParams min, ModelValueParams max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        min.validate();
        max.validate();
    }

    @Override
    public ModelValue create(Generation generation) {
        return new RandomUniformModelValue(min.create(generation), max.create(generation), generation.seed().salt(seed));
    }
}
