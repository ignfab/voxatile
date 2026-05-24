package com.ignfab.minalac.generator.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.filters.ModelFilterOnValue;
import com.ignfab.minalac.generator.parameters.models.values.ModelValueParams;

/**
 * Parameters for a filter that selects models where the model value is not absent.
 */
public class ModelFilterHasValueParams extends ModelFilterParams {
    /**
     * Model value to check (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams hasValue;

    /**
     * Creates a new {@code ModelFilterHasValueParams}.
     *
     * @param hasValue model value to check.
     */
    @ConstructorProperties("hasValue")
    public ModelFilterHasValueParams(ModelValueParams hasValue) {
        this.hasValue = hasValue;
    }

    @Override
    public void validate() {
        hasValue.validate();
    }

    @Override
    public Predicate<Model> create(Generation generation) {
        return new ModelFilterOnValue(hasValue.create(generation), v -> true);
    }
}
