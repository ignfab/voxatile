package com.ignfab.minalac.generator.parameters.models.values;

import java.beans.ConstructorProperties;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.FallbackModelValue;
import com.ignfab.minalac.generator.models.values.ModelValue;

public class FallbackModelValueParams extends ModelValueParams {
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    public List<ModelValueParams> fallback;

    @ConstructorProperties("fallback")
    public FallbackModelValueParams(List<ModelValueParams> fallback) {
        this.fallback = fallback;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (fallback.isEmpty())
            throw new IllegalArgumentException("There must be at least one model value");

        fallback.forEach(ModelValueParams::validate);
    }

    @Override
    public ModelValue create(Generation generation) {
        Iterator<ModelValueParams> iterator = fallback.iterator();

        ModelValue modelValue = iterator.next().create(generation);

        while (iterator.hasNext())
            modelValue = new FallbackModelValue(modelValue, iterator.next().create(generation));

        return modelValue;
    }
}
