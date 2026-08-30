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
 * Parameters for an "equals" operator on model value.
 */
public class ModelFilterValueEqualsParams extends ModelFilterParams {

    /**
     * Model value to test (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams value;

    /**
     * Value to test (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public double equals;

    /**
     * Creates a new {@code ModelFilterValueEqualsParams}.
     *
     * @param value model value to test.
     * @param equals value of metadata to test.
     */
    @ConstructorProperties({"value", "equals"})
    public ModelFilterValueEqualsParams(ModelValueParams value, double equals) {
        this.value = value;
        this.equals = equals;
    }

    @Override
    public void validate() {
        value.validate();
    }

    @Override
    public Predicate<Model> create(Generation generation) {
        return new ModelFilterOnValue(value.create(generation), v -> v == equals);
    }
}
