package com.ignfab.minalac.generator.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.Model;

/**
 * Parameters for combining filters with "and" operator.
 */
public class ModelFilterAndParams extends ModelFilterParams {
    /**
     * List of filters operands (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public List<ModelFilterParams> and;

    /**
     * Creates a new {@code ModelFilterAndParams}.
     *
     * @param and List of filters operands to be combined.
     */
    @ConstructorProperties({"and"})
    public ModelFilterAndParams(List<ModelFilterParams> and) {
        this.and = and;
    }

    @Override
    public void validate() {
        if (and.isEmpty())
            throw new IllegalArgumentException("There must be at least one sub-filter");

        for (ModelFilterParams filter : and) {
            filter.validate();
        }
    }

    @Override
    public Predicate<Model> create(Generation generation) {
        Iterator<ModelFilterParams> iterator = and.iterator();
        Predicate<Model> predicate = iterator.next().create(generation);

        while (iterator.hasNext())
            predicate = predicate.and(iterator.next().create(generation));

        return predicate;
    }
}
