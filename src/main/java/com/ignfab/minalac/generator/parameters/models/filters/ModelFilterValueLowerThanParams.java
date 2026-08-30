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
 * Parameters for a filter that selects models where the model value is less than a specified threshold.
 */
public class ModelFilterValueLowerThanParams extends ModelFilterParams {

    /**
     * Model value to compare (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams value;

    /**
     * Threshold value (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public double lowerThan;

    /**
     * Creates a new {@code ModelFilterValueLowerThanParams}.
     *
     * @param value model value to compare
     * @param lowerThan threshold value
     */
    @ConstructorProperties({ "value", "lowerThan" })
    public ModelFilterValueLowerThanParams(ModelValueParams value, double lowerThan) {
        this.value = value;
        this.lowerThan = lowerThan;
    }

    @Override
    public void validate() {
        value.validate();
    }

    @Override
    public Predicate<Model> create(Generation generation) {
        return new ModelFilterOnValue(value.create(generation), v -> v < lowerThan);
    }
}
